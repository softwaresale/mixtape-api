package com.mixtape.mixtapeapi.mixtape;

import com.mixtape.mixtapeapi.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Integer> {
    Optional<Reaction> findByReactorAndMixtape(Profile reactor, Mixtape mixtape);
    boolean existsByReactorAndMixtapeAndReactionType(Profile reactor, Mixtape mixtape, ReactionType reactionType);
}
