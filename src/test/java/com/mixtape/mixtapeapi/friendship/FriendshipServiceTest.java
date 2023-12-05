package com.mixtape.mixtapeapi.friendship;

import com.mixtape.mixtapeapi.notification.NotificationService;
import com.mixtape.mixtapeapi.notification.NotificationType;
import com.mixtape.mixtapeapi.playlist.PlaylistService;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.blocking.BlockedActionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FriendshipServiceTest {

    List<Profile> mockProfiles = List.of(
            new Profile("0", null, "Charlie", null),
            new Profile("1", null, "Ish", null),
            new Profile("2", null, "Alisa", null),
            new Profile("3", null, "Joey", null)
    );

    @Mock
    FriendshipRepository mockRepository;
    @Mock
    ApplicationEventPublisher mockEventPublisher;

    @Mock
    PlaylistService mockPlaylistService;

    @Mock
    NotificationService mockNotificationService;
    @Mock
    BlockedActionService mockBlockedActionService;

    FriendshipService friendshipService;

    @BeforeEach
    void setUp() {
        friendshipService = new FriendshipService(mockRepository, mockEventPublisher, mockPlaylistService, mockNotificationService, mockBlockedActionService);
    }

    @Test
    void findFriendsForProfile_getFriends_whenThereAreFriends() {

        List<Friendship> friendships = List.of(
                new Friendship("4", mockProfiles.get(0), mockProfiles.get(1)),
                new Friendship("5", mockProfiles.get(2), mockProfiles.get(0)),
                new Friendship("6", mockProfiles.get(0), mockProfiles.get(3))
        );
        Profile mockProfile = mockProfiles.get(0);
        when(mockRepository.findAllByInitiatorAndTargetNotNullOrTarget(mockProfile, mockProfile)).thenReturn(friendships);

        List<Profile> friendProfiles = friendshipService.findFriendsForProfile(mockProfile);

        assertThat(friendProfiles).containsExactly(mockProfiles.get(1), mockProfiles.get(2), mockProfiles.get(3));
    }

    @Test
    void createFriendship_createsFriendshipInvitation_whenGoodInput() {

        when(mockRepository.save(any())).then((Answer<Friendship>) answer -> (Friendship) answer.getArguments()[0]);
        when(mockBlockedActionService.isBlockedSymmetrical(mockProfiles.get(0), mockProfiles.get(1))).thenReturn(false);

        Friendship newFriendship = friendshipService.createFriendship(mockProfiles.get(0), mockProfiles.get(1));

        assertThat(newFriendship.getInitiator()).isEqualTo(mockProfiles.get(0));
        assertThat(newFriendship.getTarget()).isNull();
        verify(mockNotificationService).createNotificationFromTrigger(eq(null), eq(mockProfiles.get(0)), eq(mockProfiles.get(1)), eq("Charlie wants to be friends with you"), eq(NotificationType.FRIENDSHIP), eq(""));
    }

    @Test
    void createFriendship_fails_whenUsersAreBlocked() {
        when(mockBlockedActionService.isBlockedSymmetrical(mockProfiles.get(0), mockProfiles.get(1))).thenReturn(true);
        assertThatThrownBy(() -> {
            friendshipService.createFriendship(mockProfiles.get(0), mockProfiles.get(1));
        })
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void removeFriendship_deletesTheFriendshipAndRemovesPlaylists_whenFound() {
        Friendship friendship = new Friendship(null, mockProfiles.get(0), mockProfiles.get(1));

        when(mockRepository.findByIdAndInitiatorOrTarget(friendship.getId(), mockProfiles.get(0), mockProfiles.get(0)))
                .thenReturn(Optional.of(friendship));

        friendshipService.removeFriendshipByFriendship(mockProfiles.get(0), friendship.getId());

        verify(mockPlaylistService).removePlaylistsByBothProfiles(friendship.getInitiator(), friendship.getTarget());
        verify(mockRepository).delete(friendship);
    }
}
