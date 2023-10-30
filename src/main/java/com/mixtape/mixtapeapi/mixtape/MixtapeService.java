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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MixtapeService {

    private final MixtapeRepository repository;
    private final PlaylistService playlistService;
    private final TrackService trackService;

    public MixtapeService(MixtapeRepository repository, PlaylistService playlistService, TrackService trackService) {
        this.repository = repository;
        this.playlistService = playlistService;
        this.trackService = trackService;
    }

    public Optional<Mixtape> findMixtape(String mixtapeId) throws IOException {
        Optional<Mixtape> mixtapeOpt = repository.findById(mixtapeId);
        if (mixtapeOpt.isEmpty()) {
            return mixtapeOpt;
        }

        Mixtape mixtape = mixtapeOpt.get();
        List<TrackInfo> tracks = trackService.getTrackInfoForMixtape(mixtape);
        mixtape.setSongs(tracks);

        return Optional.of(mixtape);
    }

    public List<Mixtape> findAllMixtapesForPlaylist(Profile profile, String playlistId) throws IOException {
        Playlist playlist = playlistService.findPlaylistForProfile(profile, playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Playlist %s does not exist", playlistId)));
        return playlist.getMixtapes();
    }

    public List<Mixtape> findAllMixtapesForPlaylistByTitle(Profile profile, String playlistId, String title) throws IOException {
        Playlist playlist = playlistService.findPlaylistForProfile(profile, playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Playlist %s does not exist", playlistId)));

        // Returns mixtapes that have same title as given title
        return playlist
                .getMixtapes()
                .stream()
                .filter(mixtape -> mixtape.getName().equals(title))
                .collect(Collectors.toList());
    }

    public List<Mixtape> findAllMixtapesForPlaylistBySongName(Profile profile, String playlistId, String songName) throws IOException {
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
                .collect(Collectors.toList());
    }

    public List<Mixtape> findAllMixtapesForPlaylistByArtistName(Profile profile, String playlistId, String artistName) throws IOException {
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
                .collect(Collectors.toList());
    }

    public List<Mixtape> findAllMixtapesForPlaylistByAlbumName(Profile profile, String playlistId, String albumName) throws IOException {
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
                .collect(Collectors.toList());
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
        Mixtape savedMixtape = repository.save(newMixtape);
        playlistService.savePlaylist(parentPlaylist);
        return savedMixtape;
    }

    public void removeMixtape(Profile profile, String playlistId, String mixtapeId) throws IOException {
        // Check playlist exists in profile
        Playlist playlist = playlistService.findPlaylistForProfile(profile, playlistId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile does not own given playlist"));

        // Check mixtape exists
        Mixtape mixtape = findMixtape(mixtapeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mixtape does not exist"));

        // Check playlist contains mixtape
        if (!playlist.getMixtapes().contains(mixtape)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mixtape is not part of given playlist");
        }

        // Delete mixtape
        repository.delete(mixtape);
    }
}
