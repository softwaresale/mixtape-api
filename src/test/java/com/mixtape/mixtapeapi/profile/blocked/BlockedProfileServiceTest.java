package com.mixtape.mixtapeapi.profile.blocked;

import com.mixtape.mixtapeapi.friendship.FriendshipService;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.blocking.BlockedProfileRepository;
import com.mixtape.mixtapeapi.profile.blocking.BlockedProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BlockedProfileServiceTest {

    @Mock
    private BlockedProfileRepository mockBlockProfileRepository;
    @Mock
    private FriendshipService mockFriendshipService;

    private BlockedProfileService blockedProfileService;

    @BeforeEach
    void setUp() {
        blockedProfileService = new BlockedProfileService(mockBlockProfileRepository, mockFriendshipService);
    }

    @Test
    void blockProfile_blocksAProfile_whenNoProfileExists() {
        Profile blocker = new Profile("user1", null, "user1", null);
        Profile blockee = new Profile("user2", null, "user2", null);
        when(mockBlockProfileRepository.existsDistinctByBlockerAndBlockeeOrBlockeeAndBlocker(blocker, blockee, blocker, blockee)).thenReturn(false);
        boolean result = blockedProfileService.blockProfile(blocker, blockee);
        assertThat(result).isTrue();
        verify(mockBlockProfileRepository).save(assertArg(block -> {
            assertThat(block.getBlocker()).isEqualTo(blocker);
            assertThat(block.getBlockee()).isEqualTo(blockee);
        }));
        verify(mockFriendshipService).removeFriendshipByFriend(blocker, blockee);
    }

    @Test
    void blockProfile_doesNothing_whenAlreadyBlocked() {
        Profile blocker = new Profile("user1", null, "user1", null);
        Profile blockee = new Profile("user2", null, "user2", null);
        when(mockBlockProfileRepository.existsDistinctByBlockerAndBlockeeOrBlockeeAndBlocker(blocker, blockee, blocker, blockee)).thenReturn(true);
        boolean result = blockedProfileService.blockProfile(blocker, blockee);
        assertThat(result).isFalse();
        verify(mockBlockProfileRepository, never()).save(any());
        verify(mockFriendshipService, never()).removeFriendshipByFriend(blocker, blockee);
    }
}
