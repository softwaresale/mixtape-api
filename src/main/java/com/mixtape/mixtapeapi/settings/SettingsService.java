package com.mixtape.mixtapeapi.settings;

import com.mixtape.mixtapeapi.profile.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class SettingsService {
    private final SettingsRepository settingsRepository;

    public SettingsService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public Settings findSettingsForProfile(Profile profile) {
        return getOrCreateSettings(profile);
    }

    public Settings updatePermissionForProfile(Profile profile, boolean isPermissionNeededForPlaylists) {
        // Grab settings
        Settings settings = getOrCreateSettings(profile);

        // Set permission
        settings.setPermissionNeededForPlaylists(isPermissionNeededForPlaylists);

        // Save and return
        return settingsRepository.save(settings);
    }

    public Settings addFriendWithPermissionsToProfileSettings(Profile profile, Profile friend) {
        // Grab settings
        Settings settings = getOrCreateSettings(profile);

        // Add friend
        settings.getFriendsWithPermission().add(friend);

        return settingsRepository.save(settings);
    }

    private Settings getOrCreateSettings(Profile profile) {
        return settingsRepository.findByProfile(profile)
                .orElse(new Settings(null, profile, false, Collections.emptyList()));
    }
}
