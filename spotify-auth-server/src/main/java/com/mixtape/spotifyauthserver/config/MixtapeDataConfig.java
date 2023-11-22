package com.mixtape.spotifyauthserver.config;

import com.mixtape.mixtapeapi.profile.blocking.BlockedProfileRepository;
import com.mixtape.mixtapeapi.profile.ProfileRepository;
import com.mixtape.mixtapeapi.profile.ProfileService;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"com.mixtape.mixtapeapi","com.mixtape.spotifyauthserver"})
@EntityScan("com.mixtape.*")
public class MixtapeDataConfig {

    @Bean
    public ProfileService profileService(ProfileRepository repo) {
        return new ProfileService(repo);
    }
}
