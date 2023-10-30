package com.mixtape.mixtapeapi.friendship;

import com.mixtape.mixtapeapi.profile.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    FriendshipService friendshipService;

    @BeforeEach
    void setUp() {
        friendshipService = new FriendshipService(mockRepository, null);
    }

    @Test
    void findFriendsForProfile_getFriends_whenThereAreFriends() {

        List<Friendship> friendships = List.of(
                new Friendship("4", mockProfiles.get(0), mockProfiles.get(1)),
                new Friendship("5", mockProfiles.get(2), mockProfiles.get(0)),
                new Friendship("6", mockProfiles.get(0), mockProfiles.get(3))
        );
        Profile mockProfile = mockProfiles.get(0);
        when(mockRepository.findAllByInitiatorOrTarget(mockProfile, mockProfile)).thenReturn(friendships);

        List<Profile> friendProfiles = friendshipService.findFriendsForProfile(mockProfile);

        assertThat(friendProfiles).containsExactly(mockProfiles.get(1), mockProfiles.get(2), mockProfiles.get(3));
    }
}
