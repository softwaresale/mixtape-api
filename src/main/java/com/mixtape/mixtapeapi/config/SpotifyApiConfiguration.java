package com.mixtape.mixtapeapi.config;

import com.mixtape.mixtapeapi.MixtapeApiConstants;
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
    @Bean
    public SpotifyApi getClientCredentialsSpotifyApi() {
        return new SpotifyApi.Builder()
                .setClientId(MixtapeApiConstants.clientId)
                .setClientSecret(MixtapeApiConstants.clientSecret)
                .build();
    }

    @Bean
    public ClientCredentialsRequest getClientCredentialsRequest(SpotifyApi spotifyApi) {
        return spotifyApi.clientCredentials().build();
    }

    @Bean
    public ClientCredentials getClientCredentials(ClientCredentialsRequest clientCredentialsRequest) throws IOException, ParseException, SpotifyWebApiException {
        return clientCredentialsRequest.execute();
    }

}
