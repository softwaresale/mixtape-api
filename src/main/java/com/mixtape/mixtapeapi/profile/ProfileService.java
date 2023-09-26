package com.mixtape.mixtapeapi.profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@Service
public class ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);

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

    public Profile createProfileIfNotExists(OAuth2User oauth2User) {
        logger.info("Creating profile for first time user {}", oauth2User);
        String spotifyId = oauth2User.getName();
        String displayName = oauth2User.getAttribute("display_name");
        ArrayList<Object> imagesObj = oauth2User.getAttribute("images");
        Optional<String> profilePic = getFirstProfilePic(imagesObj);

        Profile newProfile = new Profile("", spotifyId, displayName, profilePic.orElse(""));
        return repository.save(newProfile);
    }

    private Optional<String> getFirstProfilePic(ArrayList<Object> imageObjects) {
        if (imageObjects.isEmpty()) {
            return Optional.empty();
        }

        var firstImageObject = (Map.Entry<String, ArrayList<String>>) imageObjects.get(0);
        if (firstImageObject.getValue().isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(firstImageObject.getValue().get(0));
    }
}
