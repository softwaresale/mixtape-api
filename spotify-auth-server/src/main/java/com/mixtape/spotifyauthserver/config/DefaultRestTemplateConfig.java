package com.mixtape.spotifyauthserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Provide a default rest template
 */
@Configuration
public class DefaultRestTemplateConfig {

    @Bean
    public RestTemplate defaultRestTemplate() {
        return new RestTemplate();
    }
}
