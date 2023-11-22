package com.mixtape.mixtapeapi.profile;

import com.mixtape.mixtapeapi.profile.blocking.BlockedActionService;
import com.mixtape.mixtapeapi.profile.blocking.BlockedProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfileControllerTest {
    List<Profile> mockProfiles = List.of(
            new Profile("0", null, "Charlie", null),
            new Profile("1", null, "Ish", null),
            new Profile("2", null, "Alisa", null),
            new Profile("3", null, "Joey", null)
    );

    @Mock
    ProfileService mockProfileService;
    @Mock
    BlockedProfileService mockBlockedProfileService;
    @Mock
    BlockedActionService mockBlockedActionService;

    ProfileController profileController;

    @BeforeEach
    void setUp() {
        profileController = new ProfileController(mockProfileService, mockBlockedProfileService, mockBlockedActionService);
    }

    @Test
    void getProfilesByDisplayNameFuzzy_ignoredBlockedNames() {
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("user-1");
        SecurityContext mockCtx = mock(SecurityContext.class);
        when(mockCtx.getAuthentication()).thenReturn(mockAuth);
        SecurityContextHolder.setContext(mockCtx);

        when(mockProfileService.findProfile("user-1")).thenReturn(Optional.of(mockProfiles.get(0)));
        when(mockProfileService.findProfilesByDisplayNameFuzzySearch(any())).thenReturn(mockProfiles);
        when(mockBlockedActionService.filterProfilesByBlocked(any(), any())).thenCallRealMethod();
        when(mockBlockedActionService.isBlockedSymmetrical(eq(mockProfiles.get(0)), any())).thenReturn(false, false, true, false);

        ArrayList<Profile> unblockedProfiles = new ArrayList<>(mockProfiles);
        unblockedProfiles.remove(2);

        List<Profile> results = profileController.getProfilesByDisplayNameFuzzy("");
        assertThat(results).hasSameElementsAs(unblockedProfiles);
    }
}
