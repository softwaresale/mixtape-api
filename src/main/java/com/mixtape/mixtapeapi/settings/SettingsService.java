package com.mixtape.mixtapeapi.settings;

import com.mixtape.mixtapeapi.friendship.FriendshipService;
import com.mixtape.mixtapeapi.profile.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

@Service
public class SettingsService {
    private final SettingsRepository settingsRepository;
    private final FriendshipService friendshipService;

    public SettingsService(SettingsRepository settingsRepository, FriendshipService friendshipService) {
        this.settingsRepository = settingsRepository;
        this.friendshipService = friendshipService;
    }

    public Settings updatePermissionForProfile(Profile profile, boolean isPermissionNeededForPlaylists) {
        // Grab settings
        Settings settings = getOrCreateSettings(profile);

        // Set permission
        settings.setIsPermissionNeededForPlaylists(isPermissionNeededForPlaylists);

        // Save and return
        return settingsRepository.save(settings);
    }

    public Settings addFriendWithPermissionsToProfileSettings(Profile profile, Profile friend) {
        // Grab settings
        Settings settings = getOrCreateSettings(profile);

        // Verify friend
        friendshipService.findFriendshipBetweenProfiles(profile, friend)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        // Add friend
        settings.getFriendsWithPermission().add(friend);

        return settingsRepository.save(settings);
    }

    private Settings getOrCreateSettings(Profile profile) {
        return settingsRepository.findByProfile(profile)
                .orElse(new Settings(null, profile, false, Collections.emptyList()));
    }
}
