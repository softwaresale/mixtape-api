package com.mixtape.spotifyauthserver.federation;

import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class JpaOAuth2UserHandler implements Consumer<OAuth2User> {

    private static final Logger logger = LoggerFactory.getLogger(JpaOAuth2UserHandler.class);

    private final ProfileService profileService;

    public JpaOAuth2UserHandler(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Override
    public void accept(OAuth2User oAuth2User) {
        Profile newProfile = profileService.createProfileIfNotExists(oAuth2User);
        logger.info("Created new profile with id {}: {}", newProfile.getId(), newProfile);
    }
}
