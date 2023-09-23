package com.mixtape.mixtapeapi.mixtape;

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
@RequestMapping("/api/v1/mixtape")
public class MixtapeController {
    private final MixtapeService mixtapeService;

    public MixtapeController(MixtapeService mixtapeService) {
        this.mixtapeService = mixtapeService;
    }

    @PostMapping
    public Mixtape createNew(@RequestBody Mixtape newMixtape) {
        // TODO: Add functionality to add (or create) collaborative playlist in spotify

        return mixtapeService.save(newMixtape);
    }

    @GetMapping("/{id}")
    public Mixtape getById(@PathVariable String id) {
        try {
            return mixtapeService.findMixtape(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public Mixtape update(@RequestBody Mixtape mixtape) {
        return mixtapeService.save(mixtape);
    }

}
