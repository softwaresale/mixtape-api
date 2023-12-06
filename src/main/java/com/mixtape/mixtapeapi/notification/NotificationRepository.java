package com.mixtape.mixtapeapi.notification;

import com.mixtape.mixtapeapi.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    boolean existsByInitiatorAndTargetAndNotificationType(Profile initiator, Profile target, NotificationType type);
    Optional<Notification> findByTargetAndExternalId(Profile target, String externalId);
    Optional<Notification> findByExternalId(String externalId);

    List<Notification> findAllByTarget(Profile target);

    List<Notification> findAllByInitiatorAndTarget(Profile initiator, Profile target);

    void deleteByTargetAndExternalId(Profile target, String externalId);

    void deleteByExternalId(String externalId);
    List<Notification> findAllByTargetOrInitiator(Profile profile1, Profile profile2);
}
