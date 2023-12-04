package com.mixtape.mixtapeapi.profile.suggestions;

import com.mixtape.mixtapeapi.profile.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuggestedProfileRepository extends CrudRepository<SuggestedProfile, String> {
    List<SuggestedProfile> findAllBySuggestedOrSuggestionFor(Profile profile1, Profile profile2);
    void deleteAllBySuggestedAndSuggestionForOrSuggestionForAndSuggested(Profile profile1, Profile profile2, Profile profile3, Profile profile4);
}
