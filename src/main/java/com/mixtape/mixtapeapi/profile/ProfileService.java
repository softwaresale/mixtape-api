package com.mixtape.mixtapeapi.profile;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {

    private final ProfileRepository repository;

    public ProfileService(ProfileRepository repository) {
        this.repository = repository;
    }

    public Optional<Profile> findProfile(String id) {
        return repository.findById(id);
    }

    public Profile save(Profile newProfile) {
        return repository.save(newProfile);
    }
}
