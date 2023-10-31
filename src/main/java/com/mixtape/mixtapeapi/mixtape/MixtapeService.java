package com.mixtape.mixtapeapi.mixtape;

import com.mixtape.mixtapeapi.playlist.Playlist;
import com.mixtape.mixtapeapi.playlist.PlaylistService;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.tracks.TrackInfo;
import com.mixtape.mixtapeapi.tracks.TrackService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class MixtapeService {

    private final MixtapeRepository mixtapeRepository;
    private final ReactionRepository reactionRepository;
    private final PlaylistService playlistService;
    private final TrackService trackService;

    public MixtapeService(MixtapeRepository mixtapeRepository, ReactionRepository reactionRepository, PlaylistService playlistService, TrackService trackService) {
        this.mixtapeRepository = mixtapeRepository;
        this.reactionRepository = reactionRepository;
        this.playlistService = playlistService;
        this.trackService = trackService;
    }

    public Optional<Mixtape> getById(String id) throws IOException {
        Optional<Mixtape> mixtapeOpt = mixtapeRepository.findById(id);
        if (mixtapeOpt.isEmpty()) {
            return mixtapeOpt;
        }

        Mixtape mixtape = mixtapeOpt.get();
        List<TrackInfo> tracks = trackService.getTrackInfoForMixtape(mixtape);
        mixtape.setSongs(tracks);

        return Optional.of(mixtape);
    }

    public Mixtape createMixtapeForPlaylist(Profile creator, String playlistId, Mixtape newMixtape) throws IOException {
        // find the playlist
        Playlist parentPlaylist = playlistService.findPlaylist(playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Containing playlist not found"));

        parentPlaylist.addMixtape(newMixtape);
        newMixtape.setPlaylistID(parentPlaylist.getId());
        newMixtape.setCreator(creator);
        Duration mixtapeDuration = trackService.getMixtapeDuration(newMixtape);
        newMixtape.setDurationMS(mixtapeDuration.toMillis());
        Mixtape savedMixtape = mixtapeRepository.save(newMixtape);
        playlistService.save(parentPlaylist);
        return savedMixtape;
    }

    public List<Mixtape> getAllForPlaylist(Profile profile, String playlistId) throws IOException {
        Playlist playlist = playlistService.findPlaylistForProfile(profile, playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Playlist %s does not exist", playlistId)));
        return playlist.getMixtapes();
    }

    public Mixtape createOrUpdateReactionForMixtape(String mixtapeId, Profile reactingUser, ReactionType reactionType) throws IOException {
        Mixtape mixtape = getById(mixtapeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested mixtape does not exist"));

        // This exact thing already exists, so do nothing
        if (reactionRepository.existsByReactorAndMixtapeAndReactionType(reactingUser, mixtape, reactionType)) {
            return mixtape;
        }

        Reaction upsertedReaction = reactionRepository.findByReactorAndMixtape(reactingUser, mixtape)
                .map(existing -> {
                    existing.setReactionType(reactionType);
                    return reactionRepository.save(existing);
                })
                .orElseGet(() -> {
                    Reaction newReaction = new Reaction(null, reactingUser, reactionType, mixtape);
                    return reactionRepository.save(newReaction);
                });
        // TODO I don't like repeating this one... maybe there's something more efficient
        mixtape.addReaction(upsertedReaction);
        return mixtapeRepository.save(mixtape);
    }

    public List<Reaction> getAllReactionsForMixtape(String playlistId, String mixtapeId) throws IOException {
        return getById(mixtapeId)
                .map(Mixtape::getReactions)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested mixtape does not exist"));
    }
}
