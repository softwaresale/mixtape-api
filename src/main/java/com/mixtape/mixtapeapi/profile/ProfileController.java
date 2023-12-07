package com.mixtape.mixtapeapi.profile;

import com.mixtape.mixtapeapi.AbstractRestController;
import com.mixtape.mixtapeapi.profile.blocking.BlockedActionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController extends AbstractRestController {
    private final BlockedActionService blockedActionService;

    public ProfileController(ProfileService profileService, BlockedActionService blockedActionService) {
        super(profileService);
        this.blockedActionService = blockedActionService;
    }

    @GetMapping
    public List<Profile> getProfilesByDisplayNameFuzzy(@RequestParam("displayName") String searchDisplayName) {
        Profile authenticatedUser = getAuthenticatedProfileOr404();
        List<Profile> allResults = profileService.findProfilesByDisplayNameFuzzySearch(searchDisplayName);
        return blockedActionService.filterProfilesByBlocked(authenticatedUser, allResults);
    }

    @GetMapping("/{profileId}")
    public Profile getProfile(@PathVariable String profileId) {
        return resolveProfileOr404(profileId);
    }

    @PostMapping
    public Profile createNewProfile(@RequestBody Profile newProfile) {
        return profileService.saveProfile(newProfile);
    }

    @PutMapping("/{profileId}/onboarded")
    public Profile setOnboarded(@PathVariable String profileId, @RequestParam("isOnboarded") Boolean isOnboarded) {
        Profile currentUser = resolveProfileOr404(profileId);
        currentUser.setOnboarded(isOnboarded);
        return profileService.saveProfile(currentUser);
    }
}
