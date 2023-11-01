package com.mixtape.mixtapeapi.playlist;

import com.mixtape.mixtapeapi.profile.Profile;
import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@NonNullApi
public interface PlaylistRepository extends JpaRepository<Playlist, String> {
    boolean existsById(String id);
    boolean existsByIdAndInitiatorOrIdAndTarget(String id1, Profile initiator, String id2, Profile target);

    Optional<Playlist> findById(String id);

    Optional<Playlist> findByIdAndInitiatorOrIdAndTarget(String id1, Profile initiator, String id2, Profile target);

    List<Playlist> findByInitiatorAndTargetNotNullOrTarget(Profile initiator, Profile target);

    void deleteAllByInitiatorAndTarget(Profile initiator, Profile target);
}
