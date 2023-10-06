package com.mixtape.mixtapeapi.mixtape;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("/{playlistId}/{mixtapeId}")
    public Mixtape getMixtape(@PathVariable String playlistId, @PathVariable String mixtapeId) {
        return mixtapeService.getByIds(playlistId, mixtapeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{playlistId/{mixtapeId}")
    public Mixtape updateMixtape(@PathVariable String playlistId, @PathVariable String mixtapeId, @RequestBody Mixtape newMixtape) {
        return mixtapeService.updateByIds(playlistId, mixtapeId, newMixtape)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
