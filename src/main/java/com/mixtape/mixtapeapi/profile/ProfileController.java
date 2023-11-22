package com.mixtape.mixtapeapi.profile;

import com.mixtape.mixtapeapi.AbstractRestController;
import com.mixtape.mixtapeapi.profile.blocking.BlockedActionService;
import com.mixtape.mixtapeapi.profile.blocking.BlockedProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController extends AbstractRestController {
    private final BlockedProfileService blockedProfileService;
    private final BlockedActionService blockedActionService;

    public ProfileController(ProfileService profileService, BlockedProfileService blockedProfileService, BlockedActionService blockedActionService) {
        super(profileService);
        this.blockedProfileService = blockedProfileService;
        this.blockedActionService = blockedActionService;
    }

    @GetMapping("/{profileId}")
    public Profile getProfile(@PathVariable String profileId) {
        return resolveProfileOr404(profileId);
    }

    @GetMapping
    public List<Profile> getProfilesByDisplayNameFuzzy(@RequestParam("displayName") String searchDisplayName) {
        Profile authenticatedUser = getAuthenticatedProfileOr404();
        List<Profile> allResults = profileService.findProfilesByDisplayNameFuzzySearch(searchDisplayName);
        return blockedActionService.filterProfilesByBlocked(authenticatedUser, allResults);
    }

    @PostMapping
    public Profile createNewProfile(@RequestBody Profile newProfile) {
        return profileService.saveProfile(newProfile);
    }

    @PostMapping("/{profileId}/block")
    public boolean blockProfile(@PathVariable String profileId) {
        Profile blocker = getAuthenticatedProfileOr404();
        Profile blockee = resolveProfileOr404(profileId);

        return blockedProfileService.blockProfile(blocker, blockee);
    }
}
