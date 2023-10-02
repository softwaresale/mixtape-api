package com.mixtape.mixtapeapi.mixtape;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/playlist/{playlistId}/mixtape")
public class MixtapeController {

    private final MixtapeService mixtapeService;

    public MixtapeController(MixtapeService mixtapeService) {
        this.mixtapeService = mixtapeService;
    }

    @GetMapping
    public List<Mixtape> getAllForPlaylist(@PathVariable String playlistId) {
        return mixtapeService.getAllForPlaylist(playlistId);
    }

    @PostMapping
    public Mixtape createNewMixtape(@PathVariable String playlistId, @RequestBody Mixtape newMixtape) {
        return mixtapeService.createMixtapeForPlaylist(playlistId, newMixtape);
    }

    @GetMapping("/{mixtapeId}")
    public Mixtape getMixtape(@PathVariable String playlistId, @PathVariable String mixtapeId) {
        // TODO add some sort of check with the playlist id
        return mixtapeService.getById(mixtapeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
