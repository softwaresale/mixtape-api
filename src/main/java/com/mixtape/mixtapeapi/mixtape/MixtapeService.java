package com.mixtape.mixtapeapi.mixtape;

import com.mixtape.mixtapeapi.playlist.Playlist;
import com.mixtape.mixtapeapi.playlist.PlaylistService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class MixtapeService {

    private final MixtapeRepository repository;
    private final PlaylistService playlistService;

    public MixtapeService(MixtapeRepository repository, PlaylistService playlistService) {
        this.repository = repository;
        this.playlistService = playlistService;
    }

    public Optional<Mixtape> getById(String id) {
        return repository.findById(id);
    }

    public Optional<Mixtape> getByIds(String playlistId, String mixtapeId) {
        // Grab mixtape optional
        Optional<Mixtape> optionalMixtape = getById(mixtapeId);

        // Check if present
        if (optionalMixtape.isEmpty()) return optionalMixtape;

        // Check if playlist id is right
        if (!optionalMixtape.get().getPlaylistID().equals(playlistId)) {
            return Optional.empty();
        }

        // Success
        return optionalMixtape;
    }

    public Optional<Mixtape> updateByIds(String playlistId, String mixtapeId, Mixtape newMixtape) {
        // Grab mixtape optional
        Optional<Mixtape> optionalMixtape = getByIds(playlistId, mixtapeId);

        // Check if present
        if (optionalMixtape.isEmpty()) return optionalMixtape;

        // Check playlist id
        if (!optionalMixtape.get().getPlaylistID().equals(newMixtape.getPlaylistID())) {
            return Optional.empty();
        }

        // Else update
        return Optional.of(repository.save(newMixtape));
    }

    public Mixtape createMixtapeForPlaylist(String playlistId, Mixtape newMixtape) {
        // find the playlist
        Playlist parentPlaylist = playlistService.findPlaylist(playlistId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Containing playlist not found"));

        parentPlaylist.addMixtape(newMixtape);
        Mixtape savedMixtape = repository.save(newMixtape);
        playlistService.save(parentPlaylist);
        return savedMixtape;
    }

    public Mixtape addSongToMixtape(String mixtapeId, String songId) throws ResponseStatusException {
        Mixtape existingMixtape = getById(mixtapeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        existingMixtape.addSongID(songId);

        return repository.save(existingMixtape);
    }

    public List<Mixtape> getAllForPlaylist(String playlistId) {
        return repository.findAllByParentPlaylistId(playlistId);
    }
}
