package com.mixtape.mixtapeapi.spotify;

import com.mixtape.mixtapeapi.config.ClientConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import se.michaelthelin.spotify.SpotifyApi;

@Configuration
public class SpotifyApiConfig {

    private final ClientConfigProperties clientConfigProperties;

    public SpotifyApiConfig(ClientConfigProperties clientConfigProperties) {
        this.clientConfigProperties = clientConfigProperties;
    }

    @Bean
    public SpotifyApi constructClientCredentialsSpotifyAPI() {
        return SpotifyApi.builder()
                .setClientId(clientConfigProperties.getClientId())
                .setClientSecret(clientConfigProperties.getClientSecret())
                .build();
    }

    @Profile("!prod")
    @Bean
    public SpotifyService mockSpotifyService() {
        return new MockSpotifyService();
    }

    @Profile("prod")
    @Bean
    public SpotifyService prodSpotifyService(SpotifyApi spotifyApi) {
        return new ProdSpotifyService(spotifyApi);
    }
}
