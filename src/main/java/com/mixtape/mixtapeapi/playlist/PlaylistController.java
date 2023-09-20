package com.mixtape.mixtapeapi.playlist;

import com.mixtape.mixtapeapi.invitation.Invitation;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/playlist")
public class PlaylistController {
    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @PostMapping
    public Playlist createNew(@RequestBody Invitation newInvitation) {
        return playlistService.createPlaylist(newInvitation);
    }

    @GetMapping("/{id}")
    public Playlist getById(@PathVariable String id) {
        try {
            return playlistService.findPlaylist(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public Playlist update(@RequestBody Playlist playlist) {
        return playlistService.updatePlaylist(playlist);
    }

}
