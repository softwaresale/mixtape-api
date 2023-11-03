package com.mixtape.mixtapeapi.friendship;

import com.mixtape.mixtapeapi.profile.Profile;
import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@NonNullApi
public interface FriendshipRepository extends JpaRepository<Friendship, String> {
    boolean existsByInitiatorAndTarget(Profile initiator, Profile target);
    Optional<Friendship> findById(@NonNull String id);
    Optional<Friendship> findByIdAndInitiatorOrTarget(String id, Profile initiator, Profile target);
    List<Friendship> findAllByInitiatorOrTarget(Profile initiator, Profile target);
    Optional<Friendship> findByTargetAndInitiatorOrInitiatorAndTarget(Profile target1, Profile initiator1, Profile initiator2, Profile target2);
}
