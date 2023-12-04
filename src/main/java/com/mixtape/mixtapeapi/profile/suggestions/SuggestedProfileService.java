package com.mixtape.mixtapeapi.profile.suggestions;

import com.mixtape.mixtapeapi.profile.Profile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public interface SuggestedProfileService {

    void computeSuggestionsForNewProfile(Profile newProfile, String providerToken) throws ResponseStatusException;
    List<Profile> getSuggestedProfiles(Profile givenProfile);
}
