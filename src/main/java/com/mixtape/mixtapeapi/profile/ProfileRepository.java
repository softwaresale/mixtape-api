package com.mixtape.mixtapeapi.profile;

import com.mixtape.mixtapeapi.profile.projections.SpotifyIdOnly;
import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@NonNullApi
public interface ProfileRepository extends JpaRepository<Profile, String> {
    List<SpotifyIdOnly> getAllByIdIsNotIn(Set<String> excludedIds);
    Optional<Profile> findById(@NonNull String id);
    Optional<Profile> findBySpotifyUID(@NonNull String spotifyUID);
    List<Profile> getAllByDisplayName(@NonNull String displayName);
    List<Profile> searchProfilesByDisplayNameLikeIgnoreCase(@NonNull String query);
}
