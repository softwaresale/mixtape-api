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
    void createNotificationFromTrigger_playlistCreatesProperlyFormattedMessage() {
        Playlist mockPlaylist = new Playlist();
        mockPlaylist.setId("pl1");
        mockPlaylist.setName("playlist");
        mockPlaylist.setInitiator(initiator);

        String contents = "user-1 wants to invite you to their playlist playlist";

        notificationService.createNotificationFromTrigger(mockPlaylist, initiator, target, contents);

        verify(mockRepository).save(assertArg(notification -> {
            assertThat(notification.getContents()).isEqualTo(contents);
            assertThat(notification.getInitiator()).isEqualTo(initiator);
            assertThat(notification.getTarget()).isEqualTo(target);
            assertThat(notification.getNotificationType()).isEqualTo(NotificationType.PLAYLIST);
            assertThat(notification.getExternalId()).isEqualTo("pl1");
        }));
    }

    @Test
    void createNotificationFromTrigger_friendshipCreatesProperlyFormattedNotification() {
        Friendship mockFriendship = new Friendship();
        mockFriendship.setId("f1");
        mockFriendship.setInitiator(initiator);

        String contents = "user-1 wants to be friends with you";

        notificationService.createNotificationFromTrigger(mockFriendship, initiator, target, contents);

        verify(mockRepository).save(assertArg(notification -> {
            assertThat(notification.getContents()).isEqualTo(contents);
            assertThat(notification.getInitiator()).isEqualTo(initiator);
            assertThat(notification.getTarget()).isEqualTo(target);
            assertThat(notification.getNotificationType()).isEqualTo(NotificationType.FRIENDSHIP);
            assertThat(notification.getExternalId()).isEqualTo("f1");
        }));
    }

    @Test
    void createNotificationFromTrigger_mixtapeCreatesProperlyFormattedNotification() {
        Mixtape mockMixtape = new Mixtape();
        mockMixtape.setId("m1");
        mockMixtape.setCreator(initiator);
        mockMixtape.setName("mixtape");
        Playlist playlist = new Playlist();
        playlist.setId("pl1");
        playlist.setName("playlist");

        String contents = "user-1 added the mixtape mixtape to your shared playlist playlist";

        notificationService.createNotificationFromTrigger(mockMixtape, initiator, target, contents);

        verify(mockRepository).save(assertArg(notification -> {
            assertThat(notification.getContents()).isEqualTo(contents);
            assertThat(notification.getInitiator()).isEqualTo(initiator);
            assertThat(notification.getTarget()).isEqualTo(target);
            assertThat(notification.getNotificationType()).isEqualTo(NotificationType.MIXTAPE);
            assertThat(notification.getExternalId()).isEqualTo("m1");
        }));
    }
}
