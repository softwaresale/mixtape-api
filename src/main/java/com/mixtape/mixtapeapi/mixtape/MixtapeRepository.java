package com.mixtape.mixtapeapi.mixtape;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MixtapeRepository extends JpaRepository<Mixtape, String> {
}
