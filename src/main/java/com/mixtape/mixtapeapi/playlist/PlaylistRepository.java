package com.mixtape.mixtapeapi.playlist;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@NonNullApi
public interface PlaylistRepository extends JpaRepository<Playlist, String> {
    boolean existsById(@NonNull String id);
    Optional<Playlist> findById(@NonNull String id);
}
