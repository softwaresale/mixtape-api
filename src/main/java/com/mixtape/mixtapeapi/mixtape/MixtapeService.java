package com.mixtape.mixtapeapi.mixtape;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MixtapeService {
    private final MixtapeRepository repository;

    public MixtapeService(MixtapeRepository repository) {
        this.repository = repository;
    }

    public Mixtape findMixtape(String id) throws EntityNotFoundException {
        return repository.getReferenceById(id);
    }

    public Mixtape save(Mixtape newMixtape) {
        return repository.save(newMixtape);
    }
}
