package com.mixtape.mixtapeapi.mixtape;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@NonNullApi
@Repository
public interface MixtapeRepository extends JpaRepository<Mixtape, String> {
    Optional<Mixtape> findById(@NonNull String id);

    List<Mixtape> findAllByPlaylistIdAndName(String id, String name);
}
