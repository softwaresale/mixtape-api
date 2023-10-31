package com.mixtape.mixtapeapi.notification;

import com.mixtape.mixtapeapi.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    Optional<Notification> findByTargetAndExternalId(Profile target, String externalId);
    List<Notification> findAllByTarget(Profile target);

    void deleteByTargetAndExternalId(Profile target, String externalId);
}
