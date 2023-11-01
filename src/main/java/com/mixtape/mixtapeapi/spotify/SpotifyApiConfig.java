package com.mixtape.mixtapeapi.spotify;

import com.mixtape.mixtapeapi.config.ClientConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    @Bean
    public SpotifyService spotifyService(SpotifyApi spotifyApi) {
        // TODO make this mockable
        return new ProdSpotifyService(spotifyApi);
    }
}
