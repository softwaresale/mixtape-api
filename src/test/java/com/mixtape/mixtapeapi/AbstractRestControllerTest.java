package com.mixtape.mixtapeapi;

import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AbstractRestControllerTest {
    private static class AbstractRestControllerImpl extends AbstractRestController {
        protected AbstractRestControllerImpl(ProfileService profileService) {
            super(profileService);
        }
    }

    @Mock ProfileService mockProfileService;

    AbstractRestController abstractRestController;

    @BeforeEach
    void setUp() {
        abstractRestController = new AbstractRestControllerImpl(mockProfileService);
    }

    @Test
    void resolveProfile_getsAUser_whenIdIsGiven() {
        String mockId = "some-id";
        Profile mockProfile = new Profile();
        mockProfile.setId(mockId);
        when(mockProfileService.findProfile(mockId)).thenReturn(Optional.of(mockProfile));

        Optional<Profile> result = abstractRestController.resolveProfile(mockId);
        assertThat(result).hasValue(mockProfile);

        verify(mockProfileService).findProfile(mockId);
    }

    @Test
    void resolveProfile_getsAuthenticatedUser_whenMeIsGiven() {
        String mockId = "some-id";

        Authentication mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getName()).thenReturn(mockId);
        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        Profile mockProfile = new Profile();
        mockProfile.setId(mockId);
        when(mockProfileService.findProfile(mockId)).thenReturn(Optional.of(mockProfile));

        Optional<Profile> resultingProfile = abstractRestController.resolveProfile("me");

        assertThat(resultingProfile).hasValue(mockProfile);

        verify(mockProfileService).findProfile(mockId);
    }
}
