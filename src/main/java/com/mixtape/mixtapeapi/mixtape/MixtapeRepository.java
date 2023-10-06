package com.mixtape.mixtapeapi.mixtape;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MixtapeRepository extends JpaRepository<Mixtape, String> {
    Optional<Mixtape> findById(String id);

    List<Mixtape> findAllByParentPlaylistId(String parentPlaylistId);
}
