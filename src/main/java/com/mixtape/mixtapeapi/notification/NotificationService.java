package com.mixtape.mixtapeapi.notification;

import com.mixtape.mixtapeapi.mixtape.Mixtape;
import com.mixtape.mixtapeapi.playlist.Playlist;
import com.mixtape.mixtapeapi.profile.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Stream;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public boolean notificationExistsByBothProfiles(Profile firstProfile, Profile secondProfile) {
        return notificationRepository.existsByInitiatorAndTarget(firstProfile, secondProfile) ||
                notificationRepository.existsByInitiatorAndTarget(secondProfile, firstProfile);
    }

    public List<Notification> findAllNotificationsForTarget(Profile target) {
        return notificationRepository.findAllByTarget(target);
    }

    public List<Notification> findAllNotificationByBothProfiles(Profile firstProfile, Profile secondProfile) {
        return Stream.concat(
                notificationRepository.findAllByInitiatorAndTarget(firstProfile, secondProfile).stream(),
                notificationRepository.findAllByInitiatorAndTarget(secondProfile, firstProfile).stream()
        ).toList();
    }

    public void createNotificationFromTrigger(String triggerId, Profile initiator, Profile target, String contents, NotificationType type) {
        // Create notification
        Notification notification = new Notification(null, initiator, target, contents, type, triggerId);

        // TODO: External id for mixtape notifications?

        // Save to repository
        notificationRepository.save(notification);
    }

    public void deleteNotificationByTargetAndExternalId(Profile target, String externalId) {
        // Check if target and externalId have notification
        notificationRepository.findByTargetAndExternalId(target, externalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "External id does not match a notification of your profile"));

        // Delete notification
        notificationRepository.deleteByTargetAndExternalId(target, externalId);
    }

    public void deleteNotificationsByBothProfiles(Profile firstProfile, Profile secondProfile) {
        // Grab all notifications
        notificationRepository.deleteAll(findAllNotificationByBothProfiles(firstProfile, secondProfile));
    }

    public void deleteNotificationsOfMixtapes(Playlist playlist) {
        // For each mixtape, delete if within database
        playlist
                .getMixtapes()
                .forEach(this::deleteNotificationOfMixtape);
    }

    public void deleteNotificationOfMixtape(Mixtape mixtape) {
        notificationRepository.deleteByExternalId(mixtape.getId());
    }
}
