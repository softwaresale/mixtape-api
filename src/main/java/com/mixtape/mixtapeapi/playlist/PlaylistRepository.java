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
    boolean existsByIdAndInitiatorOrTarget(String id, Profile initiator, Profile target);

    Optional<Playlist> findById(String id);

    Optional<Playlist> findByIdAndInitiatorOrTarget(String id, Profile initiator, Profile target);

    List<Playlist> findByInitiatorOrTarget(Profile initiator, Profile target);

    void deleteAllByInitiatorAndTarget(Profile initiator, Profile target);
}
