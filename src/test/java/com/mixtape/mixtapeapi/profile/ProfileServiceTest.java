package com.mixtape.mixtapeapi.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock ProfileRepository mockProfileRepository;
    @Mock BlockedProfileRepository mockBlockProfileRepository;

    ProfileService profileService;

    @BeforeEach
    void beforeEach() {
        profileService = new ProfileService(mockProfileRepository, mockBlockProfileRepository);
    }

    @Test
    void createProfileIfNotExists_works() {
        String USER_ID = "user";
        ArrayList<Map<String, Object>> imagesObj = new ArrayList<>();
        DefaultOAuth2User user = new DefaultOAuth2User(List.of(), Map.of("display_name", "mock user", "id", USER_ID, "images", imagesObj), "id");

        when(mockProfileRepository.findBySpotifyUID(USER_ID)).thenReturn(Optional.empty());
        when(mockProfileRepository.save(any(Profile.class))).then(callSite -> callSite.getArguments()[0]);

        Profile newProfile = profileService.createProfileIfNotExists(user);
        assertThat(newProfile.getSpotifyUID()).isEqualTo(USER_ID);
        assertThat(newProfile.getDisplayName()).isEqualTo("mock user");
        assertThat(newProfile.getProfilePicURL()).isEmpty();

        verify(mockProfileRepository).findBySpotifyUID(USER_ID);
        verify(mockProfileRepository).save(any());
    }

    @Test
    void createProfileIfNotExists_skipIfAlreadyExists() {
        String USER_ID = "user";
        ArrayList<Map<String, Object>> imagesObj = new ArrayList<>();
        DefaultOAuth2User user = new DefaultOAuth2User(List.of(), Map.of("display_name", "mock user", "id", USER_ID, "images", imagesObj), "id");

        Profile existingProfile = new Profile();
        existingProfile.setId("123");
        when(mockProfileRepository.findBySpotifyUID(USER_ID)).thenReturn(Optional.of(existingProfile));

        Profile profile = profileService.createProfileIfNotExists(user);
        assertThat(profile.getId()).isEqualTo("123");

        verify(mockProfileRepository).findBySpotifyUID(USER_ID);
        verify(mockProfileRepository, times(0)).save(any());
    }

    @Test
    void searchProfile_callsWithAFuzzyString() {
        profileService.findProfilesByDisplayNameFuzzySearch("user");
        verify(mockProfileRepository).searchProfilesByDisplayNameLikeIgnoreCase("%user%");
    }

    @Test
    void blockProfile_blocksAProfile_whenNoProfileExists() {
        Profile blocker = new Profile("user1", null, "user1", null);
        Profile blockee = new Profile("user2", null, "user2", null);
        when(mockBlockProfileRepository.existsDistinctByBlockerAndBlockeeOrBlockeeAndBlocker(blocker, blockee, blocker, blockee)).thenReturn(false);
        boolean result = profileService.blockProfile(blocker, blockee);
        assertThat(result).isTrue();
        verify(mockBlockProfileRepository).save(assertArg(block -> {
            assertThat(block.getBlocker()).isEqualTo(blocker);
            assertThat(block.getBlockee()).isEqualTo(blockee);
        }));
    }

    @Test
    void blockProfile_doesNothing_whenAlreadyBlocked() {
        Profile blocker = new Profile("user1", null, "user1", null);
        Profile blockee = new Profile("user2", null, "user2", null);
        when(mockBlockProfileRepository.existsDistinctByBlockerAndBlockeeOrBlockeeAndBlocker(blocker, blockee, blocker, blockee)).thenReturn(true);
        boolean result = profileService.blockProfile(blocker, blockee);
        assertThat(result).isFalse();
        verify(mockBlockProfileRepository, never()).save(any());
    }
}
