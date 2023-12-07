package com.mixtape.mixtapeapi.settings;

import com.mixtape.mixtapeapi.AbstractRestController;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile/{profileId}/settings")
public class SettingsController extends AbstractRestController {
    private final SettingsService settingsService;

    public SettingsController(ProfileService profileService, SettingsService settingsService) {
        super(profileService);
        this.settingsService = settingsService;
    }

    @PutMapping
    public Settings updatePermissionForProfile(@PathVariable String profileId, @RequestParam boolean isPermissionNeededForPlaylists) {
        // Grab Profile
        Profile profile = resolveProfileOr404(profileId);

        // Update permission
        return settingsService.updatePermissionForProfile(profile, isPermissionNeededForPlaylists);
    }

    @PutMapping ("/{friendId}")
    Settings updateFriendsWithPermissionForProfile(@PathVariable String profileId, @PathVariable String friendId) {
        // Grab profiles
        Profile profile = resolveProfileOr404(profileId);
        Profile friendProfile = resolveProfileOr404(friendId);

        // Update permission for friends
        return settingsService.addFriendWithPermissionsToProfileSettings(profile, friendProfile);
    }
}
