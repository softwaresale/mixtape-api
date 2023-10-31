package com.mixtape.mixtapeapi.profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public Optional<Profile> findProfileBySpotifyId(String id) { return repository.findBySpotifyUID(id); }

    public List<Profile> searchProfilesByDisplayName(String displayName) {
        String queryValue = String.format("%%%s%%", displayName);
        return repository.searchProfilesByDisplayNameLikeIgnoreCase(queryValue);
    }

    public Profile save(Profile newProfile) {
        return repository.save(newProfile);
    }

    public Profile createProfileIfNotExists(OAuth2User oauth2User) {
        logger.info("Creating profile for first time user {}", oauth2User);
        Optional<Profile> existingProfile = findProfileBySpotifyId(oauth2User.getName());
        if (existingProfile.isPresent()) {
            logger.info("Profile {} already exists for spotify user {}", existingProfile.get().getId(), oauth2User.getName());
            return existingProfile.get();
        }
        String spotifyId = oauth2User.getName();
        String displayName = oauth2User.getAttribute("display_name");
        ArrayList<Map<String, Object>> imagesObjs = oauth2User.getAttribute("images");
        Optional<String> profilePic = getFirstProfilePic(imagesObjs);

        Profile newProfile = new Profile("", spotifyId, displayName, profilePic.orElse(""));
        return repository.save(newProfile);
    }

    private Optional<String> getFirstProfilePic(@Nullable ArrayList<Map<String, Object>> imageObjects) {
        if (imageObjects == null) {
            return Optional.empty();
        }

        if (imageObjects.isEmpty()) {
            return Optional.empty();
        }

        var firstImageObject = imageObjects.get(0);
        return Optional.ofNullable(firstImageObject.get("url"))
                .map(obj -> (String) obj);
    }

    public List<Profile> findAllUsersByDisplayName(String displayName) {
        return repository.getAllByDisplayName(displayName);
    }
}
