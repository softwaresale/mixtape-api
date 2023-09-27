package com.mixtape.mixtapeapi.friendship;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@NonNullApi
public interface FriendshipRepository extends JpaRepository<Friendship, String> {
    Optional<Friendship> findById(@NonNull String id);
}
