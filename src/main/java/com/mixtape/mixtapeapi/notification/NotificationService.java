package com.mixtape.mixtapeapi.notification;

import com.mixtape.mixtapeapi.friendship.Friendship;
import com.mixtape.mixtapeapi.mixtape.Mixtape;
import com.mixtape.mixtapeapi.playlist.Playlist;
import com.mixtape.mixtapeapi.profile.Profile;
import org.springframework.stereotype.Service;

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
        notificationRepository.deleteByTargetAndExternal_id(target, playlist.getId());
    }

    public void deleteNotificationFromFriendship(Friendship friendship, Profile target) {
        notificationRepository.deleteByTargetAndExternal_id(target, friendship.getId());
    }
}
