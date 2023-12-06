package com.mixtape.mixtapeapi.profile.suggestions;

import com.mixtape.mixtapeapi.friendship.FriendshipService;
import com.mixtape.mixtapeapi.notification.NotificationService;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import com.mixtape.mixtapeapi.profile.blocking.BlockedProfileService;
import com.mixtape.mixtapeapi.spotify.SpotifyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SuggestedProfileServiceTest {

    @Mock
    ProfileService mockProfileService;

    @Mock
    FriendshipService mockFriendshipService;

    @Mock
    BlockedProfileService mockBlockedProfileService;

    @Mock
    SuggestedProfileRepository mockRepository;

    @Mock
    SpotifyService mockSpotifyService;

    @Mock
    NotificationService mockNotificationService;

    @InjectMocks
    SuggestedProfileService suggestedProfileService;

    @Test
    void computeSuggestionsForNewProfile_shouldComputeAllSuggestions_withNoFilters() {

        Profile mockProfile = new Profile();
        mockProfile.setId("new-user");
        String providerToken = "provider";

        when(mockBlockedProfileService.getBlockedProfiles(mockProfile)).thenReturn(List.of());
        when(mockFriendshipService.findFriendsForProfile(mockProfile)).thenReturn(List.of());
        when(mockProfileService.getSpotifyIDsForAllUsersExcept(any())).thenReturn(List.of("user-1", "user-2"));
        when(mockSpotifyService.checkFollowsAnyUsers(eq(providerToken), any())).thenReturn(List.of("user-1"));

        suggestedProfileService.computeSuggestionsForNewProfile(mockProfile, providerToken);

        verify(mockBlockedProfileService).getBlockedProfiles(mockProfile);
        verify(mockFriendshipService).findFriendsForProfile(mockProfile);
        verify(mockProfileService).getSpotifyIDsForAllUsersExcept(assertArg(ignored -> {
            assertThat(ignored).contains("new-user");
        }));
        verify(mockSpotifyService).checkFollowsAnyUsers(providerToken, List.of("user-1", "user-2"));
    }
}
