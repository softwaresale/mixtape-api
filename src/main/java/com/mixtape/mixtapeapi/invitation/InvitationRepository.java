package com.mixtape.mixtapeapi.invitation;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@NonNullApi
public interface InvitationRepository extends JpaRepository<Invitation, String> {
    Optional<Invitation> findById(@NonNull String id);
}
