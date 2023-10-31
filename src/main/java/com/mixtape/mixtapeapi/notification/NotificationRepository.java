package com.mixtape.mixtapeapi.notification;

import com.mixtape.mixtapeapi.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    Optional<Notification> findByTargetAndExternal_id(Profile target, String external_id);
    List<Notification> findAllByTarget(Profile target);

    void deleteByTargetAndExternal_id(Profile target, String external_id);
}
