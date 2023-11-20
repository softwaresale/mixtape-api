package com.mixtape.mixtapeapi.search;

import com.mixtape.mixtapeapi.AbstractRestController;
import com.mixtape.mixtapeapi.profile.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile/{profileId}/search")
public class SearchController extends AbstractRestController {
    private final SearchService searchService;

    public SearchController(ProfileService profileService, SearchService searchService) {
        super(profileService);
        this.searchService = searchService;
    }

    @GetMapping("/recent-tracks")
    public List<TrackSimplified> getRecentlyListenedToTracks(@PathVariable String profileId) {
        // Check profile id matches logged-in user
        if (!profileId.equals("me")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This endpoint can only be called as current user");
        }

        // Grab provider token for user
        String providerToken = getProviderToken()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Current user does not have a provider token"));

        // Grab tracks and return
        return searchService.findRecentlyListenedToTracks(providerToken);
    }
}
