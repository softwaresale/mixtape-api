package com.mixtape.mixtapeapi.profile.suggestions;

import com.mixtape.mixtapeapi.AbstractRestController;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile/{profileId}/suggestions")
public class SuggestionsController extends AbstractRestController {
    private final SuggestedProfileService suggestedProfileService;

    public SuggestionsController(ProfileService profileService, SuggestedProfileService suggestedProfileService) {
        super(profileService);
        this.suggestedProfileService = suggestedProfileService;
    }

    @GetMapping
    public List<Profile> getSuggestedUsers(@PathVariable String profileId) {
        Profile callingProfile = resolveProfileOr404(profileId);
        String providerToken = getProviderTokenOr500();
        return suggestedProfileService.computeAndGetSuggestedProfiles(callingProfile, providerToken);
    }

    @PostMapping("/compute")
    public void computeSuggestedProfilesForNewProfile(@PathVariable String profileId) {
        Profile createdProfile = resolveProfileOr404(profileId);
        String providerToken = getProviderToken().orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Not provider token found"));

        suggestedProfileService.computeSuggestionsForNewProfile(createdProfile, providerToken);
    }
}
