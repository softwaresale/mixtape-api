package com.mixtape.mixtapeapi.mixtape;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MixtapeService {
    private final MixtapeRepository mixtapeRepository;

    public MixtapeService(MixtapeRepository mixtapeRepository) {
        this.mixtapeRepository = mixtapeRepository;
    }

    public Optional<Mixtape> findMixtape(String id) throws EntityNotFoundException {
        return mixtapeRepository.findById(id);
    }

    public Mixtape save(Mixtape newMixtape) {
        return mixtapeRepository.save(newMixtape);
    }

    public Optional<Mixtape> updateMixtape(Mixtape mixtape, String id) {
        // Create Optional
        Optional<Mixtape> optionalMixtape = Optional.empty();

        // If exists, add to optional
        if (mixtapeRepository.existsById(id)) {
            optionalMixtape = Optional.of(mixtapeRepository.save(mixtape));
        }

        // Return final optional
        return optionalMixtape;
    }

    public List<Mixtape> findAllMixtapesByPlaylistId(String playlistId) {
        return mixtapeRepository.findAllByPlaylistID(playlistId);
    }
}
