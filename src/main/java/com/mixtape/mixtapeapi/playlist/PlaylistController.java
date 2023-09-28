package com.mixtape.mixtapeapi.playlist;

import com.mixtape.mixtapeapi.mixtape.Mixtape;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/playlist")
public class PlaylistController {
    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }


    @GetMapping("/{id}")
    public Playlist getById(@PathVariable String id) {
        return playlistService.findPlaylist(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public Playlist update(@RequestBody Playlist playlist, @PathVariable String id) {
        return playlistService.updatePlaylist(playlist, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}/mixtapes")
    public List<Mixtape> getMixtapesById(@PathVariable String id) {
        return playlistService.findMixtapesOfPlaylist(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }

}
