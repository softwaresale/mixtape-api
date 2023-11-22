package com.mixtape.mixtapeapi.profile.blocking;

import com.mixtape.mixtapeapi.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockedProfileRepository extends JpaRepository<BlockedProfile, String> {
    List<BlockedProfile> findBlockedProfileByBlocker(Profile blocker);
    boolean existsDistinctByBlockerAndBlockeeOrBlockeeAndBlocker(Profile blocker1, Profile blockee1, Profile blockee2, Profile blocker2);
}
