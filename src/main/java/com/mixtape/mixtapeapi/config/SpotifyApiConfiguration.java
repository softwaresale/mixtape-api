package com.mixtape.mixtapeapi.config;

import org.apache.hc.core5.http.ParseException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.IOException;

@Configuration
public class SpotifyApiConfiguration {

    private final ClientConfiguration clientConfiguration;

    public SpotifyApiConfiguration(ClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
    }

    @Bean
    public ClientCredentialsRequest clientCredentialsRequest() {
        return new SpotifyApi.Builder()
                .setClientId(clientConfiguration.getClientId())
                .setClientSecret(clientConfiguration.getClientSecret())
                .build()
                .clientCredentials()
                .build();
    }

    @Bean
    public ClientCredentials clientCredentials(ClientCredentialsRequest clientCredentialsRequest) throws IOException, ParseException, SpotifyWebApiException {
        return clientCredentialsRequest.execute();
    }

    @Bean
    public SpotifyApi clientCredentialsSpotifyApi(ClientCredentials clientCredentials) {
        return SpotifyApi
                .builder()
                .setAccessToken(clientCredentials.getAccessToken())
                .build();
    }

}
