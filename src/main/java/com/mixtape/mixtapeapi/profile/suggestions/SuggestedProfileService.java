package com.mixtape.mixtapeapi.profile.suggestions;

import com.mixtape.mixtapeapi.friendship.FriendshipService;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import com.mixtape.mixtapeapi.profile.blocking.BlockedProfileService;
import com.mixtape.mixtapeapi.spotify.SpotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SuggestedProfileService {
    private static final Logger logger = LoggerFactory.getLogger(SuggestedProfileService.class);

    private final ProfileService profileService;
    private final FriendshipService friendshipService;
    private final BlockedProfileService blockedProfileService;
    private final SuggestedProfileRepository repository;
    private final SpotifyService spotifyService;

    public SuggestedProfileService(ProfileService profileService, FriendshipService friendshipService, BlockedProfileService blockedProfileService, SuggestedProfileRepository repository, SpotifyService spotifyService) {
        this.profileService = profileService;
        this.friendshipService = friendshipService;
        this.blockedProfileService = blockedProfileService;
        this.repository = repository;
        this.spotifyService = spotifyService;
    }

    public List<Profile> computeAndGetSuggestedProfiles(Profile newProfile, String providerToken) {
        // figure out what profiles we already are friends with
        List<Profile> existingSuggestions = this.getSuggestedProfiles(newProfile);
        Set<String> existingSuggestedUsers = existingSuggestions.stream()
                .map(Profile::getId)
                .collect(Collectors.toSet());

        // Compute stuff
        computeSuggestionsForNewProfile(newProfile, providerToken, existingSuggestedUsers);

        // fetch everything, including new stuff
        return getSuggestedProfiles(newProfile);
    }

    public void computeSuggestionsForNewProfile(Profile newProfile, String providerToken) throws ResponseStatusException {
        computeSuggestionsForNewProfile(newProfile, providerToken, Set.of());
    }

    public void computeSuggestionsForNewProfile(Profile newProfile, String providerToken, Set<String> excludeProfileIDs) throws ResponseStatusException {

        logger.info("Computing suggested users for profile '{}' with provider token '{}'", newProfile, providerToken);

        // get a set of candidates to ignore
        Set<String> excluded = getCandidateFilters(newProfile, excludeProfileIDs);

        List<String> spotifyIDs = profileService.getSpotifyIDsForAllUsersExcept(excluded);
        List<String> followedUsers = spotifyService.checkFollowsAnyUsers(providerToken, spotifyIDs);

        List<SuggestedProfile> suggestedProfiles = followedUsers.stream()
                .map(this.profileService::findProfileBySpotifyId)
                .flatMap(Optional::stream)
                .map(suggestedProfile -> new SuggestedProfile(null, newProfile, suggestedProfile))
                .toList();

        repository.saveAll(suggestedProfiles);
    }

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

    public void deleteSuggestionsBetweenUsers(Profile profile1, Profile profile2) {
        this.repository.deleteAllBySuggestedAndSuggestionForOrSuggestionForAndSuggested(profile1, profile2, profile1, profile2);
    }

    private Set<String> getCandidateFilters(Profile newProfile, Set<String> ignoredUsers) {

        Set<String> allIgnored = new HashSet<>(ignoredUsers);
        allIgnored.add(newProfile.getId());

        // don't suggest blocked profiles
        this.blockedProfileService.getBlockedProfiles(newProfile).stream()
                .map(Profile::getId)
                .forEach(allIgnored::add);

        // don't suggest people you're already friends with
        this.friendshipService.findFriendsForProfile(newProfile).stream()
                .map(Profile::getId)
                .forEach(allIgnored::add);

        return allIgnored;
    }
}
