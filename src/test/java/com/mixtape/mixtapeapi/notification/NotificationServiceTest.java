package com.mixtape.mixtapeapi.notification;

import com.mixtape.mixtapeapi.friendship.Friendship;
import com.mixtape.mixtapeapi.mixtape.Mixtape;
import com.mixtape.mixtapeapi.playlist.Playlist;
import com.mixtape.mixtapeapi.profile.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    Profile initiator = new Profile("user-1", null, "user-1", null);
    Profile target = new Profile("user-2", null, "user-2", null);

    @Mock
    NotificationRepository mockRepository;

    NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(mockRepository);
    }

    @Test
    void createNotificationFromPlaylist_createsProperlyFormattedMessage() {
        Playlist mockPlaylist = new Playlist();
        mockPlaylist.setId("pl1");
        mockPlaylist.setName("playlist");
        mockPlaylist.setInitiator(initiator);

        notificationService.createNotificationFromPlaylist(mockPlaylist, target);

        verify(mockRepository).save(assertArg(notification -> {
            assertThat(notification.getContents()).isEqualTo("user-1 wants to invite you to their playlist playlist");
            assertThat(notification.getTarget()).isEqualTo(target);
            assertThat(notification.getNotificationType()).isEqualTo(NotificationType.PLAYLIST);
            assertThat(notification.getExternalId()).isEqualTo("pl1");
        }));
    }

    @Test
    void createNotificationFromFriendship_createsProperlyFormattedNotification() {
        Friendship mockFriendship = new Friendship();
        mockFriendship.setId("f1");
        mockFriendship.setInitiator(initiator);

        notificationService.createNotificationFromFriendship(mockFriendship, target);

        verify(mockRepository).save(assertArg(notification -> {
            assertThat(notification.getContents()).isEqualTo("user-1 wants to be friends with you");
            assertThat(notification.getTarget()).isEqualTo(target);
            assertThat(notification.getNotificationType()).isEqualTo(NotificationType.FRIENDSHIP);
            assertThat(notification.getExternalId()).isEqualTo("f1");
        }));
    }

    @Test
    void createNotificationFromMixtape_createsProperlyFormattedNotification() {
        Mixtape mockMixtape = new Mixtape();
        mockMixtape.setId("m1");
        mockMixtape.setCreator(initiator);
        mockMixtape.setName("mixtape");

        notificationService.createNotificationFromMixtape(mockMixtape, target, "playlist");

        verify(mockRepository).save(assertArg(notification -> {
            assertThat(notification.getContents()).isEqualTo("user-1 added the mixtape mixtape to your shared playlist playlist");
            assertThat(notification.getTarget()).isEqualTo(target);
            assertThat(notification.getNotificationType()).isEqualTo(NotificationType.MIXTAPE);
            assertThat(notification.getExternalId()).isEqualTo("m1");
        }));
    }
}
