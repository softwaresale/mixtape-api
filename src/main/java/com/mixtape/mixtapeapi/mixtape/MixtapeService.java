package com.mixtape.mixtapeapi.mixtape;

import com.mixtape.mixtapeapi.notification.NotificationService;
import com.mixtape.mixtapeapi.playlist.Playlist;
import com.mixtape.mixtapeapi.playlist.PlaylistService;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.tracks.TrackInfo;
import com.mixtape.mixtapeapi.tracks.TrackService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class MixtapeService {

    private final MixtapeRepository mixtapeRepository;
    private final ReactionRepository reactionRepository;
    private final PlaylistService playlistService;
    private final TrackService trackService;
    private final NotificationService notificationService;

    public MixtapeService(MixtapeRepository mixtapeRepository, ReactionRepository reactionRepository, PlaylistService playlistService, TrackService trackService, NotificationService notificationService) {
        this.mixtapeRepository = mixtapeRepository;
        this.reactionRepository = reactionRepository;
        this.playlistService = playlistService;
        this.trackService = trackService;
        this.notificationService = notificationService;
    }

    public Optional<Mixtape> findMixtape(String mixtapeId) {
        Optional<Mixtape> mixtapeOpt = mixtapeRepository.findById(mixtapeId);
        if (mixtapeOpt.isEmpty()) {
            return mixtapeOpt;
        }

        Mixtape mixtape = mixtapeOpt.get();
        List<TrackInfo> tracks = trackService.getTrackInfoForMixtape(mixtape);
        mixtape.setSongs(tracks);

        return Optional.of(mixtape);
    }

    public List<Mixtape> findAllMixtapesForPlaylist(Profile profile, String playlistId) {
        Playlist playlist = playlistService.findPlaylistForProfile(profile, playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Playlist %s does not exist", playlistId)));
        return playlist.getMixtapes();
    }

    public List<Mixtape> findAllMixtapesForPlaylistByTitle(Profile profile, String playlistId, String title) {
        // Verify profile has playlist
        playlistService.findPlaylistForProfile(profile, playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Playlist %s does not exist", playlistId)));

        // Returns mixtapes that have same title as given title
        return trackService.inflateMixtapes(mixtapeRepository
                .findAllByPlaylistIDAndName(playlistId, title));
    }

    public List<Mixtape> findAllMixtapesForPlaylistBySongName(Profile profile, String playlistId, String songName) {
        Playlist playlist = playlistService.findPlaylistForProfile(profile, playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Playlist %s does not exist", playlistId)));

        // Returns mixtapes that have any songs with same name as songName
        return playlist
                .getMixtapes()
                .stream()
                .filter(mixtape -> mixtape
                        .getSongs()
                        .stream()
                        .map(TrackInfo::getName)
                        .anyMatch(songName::equals))
                .toList();
    }

    public List<Mixtape> findAllMixtapesForPlaylistByArtistName(Profile profile, String playlistId, String artistName) {
        Playlist playlist = playlistService.findPlaylistForProfile(profile, playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Playlist %s does not exist", playlistId)));

        // Returns mixtapes that have any artists with same name as artistName
        return playlist
                .getMixtapes()
                .stream()
                .filter(mixtape -> mixtape
                        .getSongs()
                        .stream()
                        .map(TrackInfo::getArtistNames)
                        .flatMap(Collection::stream)
                        .anyMatch(artistName::equals))
                .toList();
    }

    public List<Mixtape> findAllMixtapesForPlaylistByAlbumName(Profile profile, String playlistId, String albumName) {
        Playlist playlist = playlistService.findPlaylistForProfile(profile, playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Playlist %s does not exist", playlistId)));

        // Returns mixtapes that have any albums with same name as albumName
        return playlist
                .getMixtapes()
                .stream()
                .filter(mixtape -> mixtape
                        .getSongs()
                        .stream()
                        .map(TrackInfo::getAlbumName)
                        .anyMatch(albumName::equals))
                .toList();
    }

    public List<Reaction> findAllReactionsForMixtape(String playlistId, String mixtapeId) {
        return findMixtape(mixtapeId)
                .map(Mixtape::getReactions)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested mixtape does not exist"));
    }

    public Mixtape createMixtapeForPlaylist(Profile creator, String playlistId, Mixtape newMixtape) {
        // Find the playlist
        Playlist playlist = playlistService.findPlaylist(playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Containing playlist not found"));

        // Add to playlist and set items
        playlist.addMixtape(newMixtape);
        newMixtape.setPlaylistID(playlist.getId());
        newMixtape.setCreator(creator);
        newMixtape.setDurationMS(trackService
                .getMixtapeDuration(newMixtape)
                .toMillis());

        // Save mixtape
        Mixtape mixtape = mixtapeRepository.save(newMixtape);

        // Update playlist
        playlistService.savePlaylist(playlist);

        // Grab target of notification
        Profile notificationTarget = playlist.getInitiator().equals(creator)
                ? playlist.getTarget() : playlist.getInitiator();

        // Create contents and notification
        String contents = String.format("%s created a mixtape %s for your shared playlist %s", creator.getDisplayName(), mixtape.getName(), playlist.getName());
        notificationService.createNotificationFromTrigger(mixtape, creator, notificationTarget, contents);

        // Return inflated mixtape with songs
        return trackService.inflateMixtape(mixtape);
    }

    public Mixtape createOrUpdateReactionForMixtape(String mixtapeId, Profile reactingUser, ReactionType reactionType) {
        Mixtape mixtape = findMixtape(mixtapeId)
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
        mixtape.addReaction(upsertedReaction);
        return mixtapeRepository.save(mixtape);
    }

    public void removeMixtape(Profile profile, String playlistId, String mixtapeId) {
        // Check playlist exists in profile
        Playlist playlist = playlistService.findPlaylistForProfile(profile, playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile does not own given playlist"));

        // Check mixtape exists
        Mixtape mixtape = findMixtape(mixtapeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mixtape does not exist"));

        // Check playlist contains mixtape
        if (!playlist.getMixtapes().contains(mixtape)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mixtape is not part of given playlist");
        }

        // Remove the other side of the relationship
        playlist.getMixtapes().remove(mixtape);
        playlistService.savePlaylist(playlist);

        // Delete notification of mixtape
        notificationService.deleteNotificationOfMixtape(mixtape);

        // Delete mixtape
        mixtapeRepository.delete(mixtape);
    }
}
