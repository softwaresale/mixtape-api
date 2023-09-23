package com.mixtape.spotifyauthserver;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.time.Duration;
import java.time.Instant;

import static org.mockito.Mockito.mock;

public final class MockOAuth2AuthorizationHelper {

    public static final String MOCK_CLIENT_REGISTRATION_ID = "client-id";
    public static final String MOCK_PRINCIPAL_NAME = "user";
    public static final String MOCK_ACCESS_TOKEN_VALUE = "access_token_value";
    public static final String MOCK_REFRESH_TOKEN_VALUE = "refresh_token_value";
    public static final String MOCK_CLIENT_SECRET = "client_secret";
    public static final String MOCK_CLIENT_IDP_ID = "client_idp_id";
    public static final String MOCK_CLIENT_IDP_SECRET = "mock_client_idp_secret";
    public static final String MOCK_TOKEN_URI = "token://uri";
    public static final String MOCK_REFRESHED_ACCESS_TOKEN = "refreshed-access-token";

    public static final RegisteredClient MOCK_REGISTERED_CLIENT = RegisteredClient.withId(MOCK_CLIENT_REGISTRATION_ID)
            .clientId(MOCK_CLIENT_REGISTRATION_ID)
                .clientSecret(MOCK_CLIENT_SECRET)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("redirect://uri")
                .build();
    public static final ClientRegistration MOCK_CLIENT_REGISTRATION = ClientRegistration.withRegistrationId(MOCK_CLIENT_IDP_ID)
            .tokenUri(MOCK_TOKEN_URI)
                .authorizationUri("authorization://auth")
                .redirectUri("redirect://uri")
                .clientId(MOCK_CLIENT_IDP_ID)
                .clientSecret(MOCK_CLIENT_IDP_SECRET)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();
    public static final OAuth2AccessToken MOCK_OAUTH2_ACCESS_TOKEN = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            MOCK_ACCESS_TOKEN_VALUE,
            Instant.now().minus(Duration.ofMinutes(60)), // mock token was issued an hour ago
            Instant.now().plus(Duration.ofHours(1)) // and expires in an hour
            );
    public static final OAuth2RefreshToken MOCK_OAUTH2_REFRESH_TOKEN = new OAuth2RefreshToken(
            MOCK_REFRESH_TOKEN_VALUE,
            Instant.now().minus(Duration.ofMinutes(60)),
            null // has no expiry
            );

    public static final OAuth2AuthenticationToken MOCK_OAUTH2_AUTHENTICATION_TOKEN = new OAuth2AuthenticationToken(
            mock(OAuth2User.class), // make this not NPE
            null,
            MOCK_CLIENT_IDP_ID
    );
    public static final OAuth2Authorization MOCK_OAUTH2_AUTHORIZATION = OAuth2Authorization.withRegisteredClient(MOCK_REGISTERED_CLIENT)
            .accessToken(MOCK_OAUTH2_ACCESS_TOKEN)
            .refreshToken(MOCK_OAUTH2_REFRESH_TOKEN)
            .attribute(java.security.Principal.class.getName(), MOCK_OAUTH2_AUTHENTICATION_TOKEN)
            .principalName(MOCK_PRINCIPAL_NAME)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .build();
}
