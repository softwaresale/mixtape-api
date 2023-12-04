package com.mixtape.mixtapeapi.profile.suggestions;

import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import com.mixtape.mixtapeapi.spotify.SpotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class SuggestedProfileServiceImpl implements SuggestedProfileService {
    private static final Logger logger = LoggerFactory.getLogger(SuggestedProfileServiceImpl.class);

    private final ProfileService profileService;
    private final SuggestedProfileRepository repository;
    private final SpotifyService spotifyService;

    public SuggestedProfileServiceImpl(ProfileService profileService, SuggestedProfileRepository repository, SpotifyService spotifyService) {
        this.profileService = profileService;
        this.repository = repository;
        this.spotifyService = spotifyService;
    }

    @Override
    public void computeSuggestionsForNewProfile(Profile newProfile, String providerToken) throws ResponseStatusException {

        logger.info("Computing suggested users for profile '{}' with provider token '{}'", newProfile, providerToken);

        List<String> spotifyIDs = profileService.getSpotifyIDsForAllUsersExcept(newProfile.getId());
        List<String> followedUsers = spotifyService.checkFollowsAnyUsers(providerToken, spotifyIDs);

        List<SuggestedProfile> suggestedProfiles = followedUsers.stream()
                .map(this.profileService::findProfileBySpotifyId)
                .flatMap(Optional::stream)
                .map(suggestedProfile -> new SuggestedProfile(null, newProfile, suggestedProfile))
                .toList();

        repository.saveAll(suggestedProfiles);
    }

    @Override
    public List<Profile> getSuggestedProfiles(Profile givenProfile) {
        return this.repository.findAllBySuggestedOrSuggestionFor(givenProfile, givenProfile).stream()
                .map(suggestedProfile -> {
                    if (suggestedProfile.getSuggested().equals(givenProfile)) {
                        return suggestedProfile.getSuggestionFor();
                    } else {
                        return suggestedProfile.getSuggested();
                    }
                })
                .toList();
    }
}
