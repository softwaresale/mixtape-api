package com.mixtape.mixtapeapi.notification;

import com.mixtape.mixtapeapi.friendship.Friendship;
import com.mixtape.mixtapeapi.mixtape.Mixtape;
import com.mixtape.mixtapeapi.playlist.Playlist;
import com.mixtape.mixtapeapi.profile.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> findAllNotificationsForProfile(Profile profile) {
        return notificationRepository.findAllByTarget(profile);
    }

    public void createNotificationFromPlaylist(Playlist playlist, Profile target) {
        // Create contents for notification
        String contents = "TODO";

        // Create notification
        Notification notification = new Notification("", target, contents, NotificationType.PLAYLIST, playlist.getId());

        // Save to repository
        notificationRepository.save(notification);
    }

    public void createNotificationFromFriendship(Friendship friendship, Profile target) {
        // Create contents for notification
        String contents = "TODO";

        // Create notification
        Notification notification = new Notification("", target, contents, NotificationType.FRIENDSHIP, friendship.getId());

        // Save to repository
        notificationRepository.save(notification);

    }

    public void createNotificationFromMixtape(Mixtape mixtape, Profile target) {
        // Create contents for notification
        String contents = "TODO";

        // Create notification
        Notification notification = new Notification("", target, contents, NotificationType.MIXTAPE, mixtape.getId());

        // Save to repository
        notificationRepository.save(notification);
    }

    public void deleteNotificationFromPlaylist(Playlist playlist, Profile target) {
        // Check if target was invited to playlist
        notificationRepository.findByTargetAndExternal_id(target, playlist.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "This playlist was not for given profile"));

        // Delete notification
        notificationRepository.deleteByTargetAndExternal_id(target, playlist.getId());
    }

    public void deleteNotificationFromFriendship(Friendship friendship, Profile target) {
        // Check if target was invited to playlist
        notificationRepository.findByTargetAndExternal_id(target, friendship.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "This friendship was not for given profile"));

        // Delete notification
        notificationRepository.deleteByTargetAndExternal_id(target, friendship.getId());
    }
}
