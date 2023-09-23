package com.mixtape.spotifyauthserver.data.authclient;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

/**
 * Shared utilities for working with {@link AuthorizedClient} models. Mostly used for converting entities
 *
 * @author Charlie Sale
 * @since 0.0.1
 */
public class AuthorizedClientUtils {

    /**
     * Convert an authorized client entity into an OAuth2AuthorizedClient
     * @param authorizedClient The entity to convert
     * @param clientRegistration The client registration associated with this authorized client entity
     * @return An OAuth2AuthorizedClient object
     */
    public static OAuth2AuthorizedClient convertAuthorizedClientToOAuth2AuthorizedClient(AuthorizedClient authorizedClient, @NonNull ClientRegistration clientRegistration) {
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, authorizedClient.getProviderToken(), authorizedClient.getIssuedAt(), authorizedClient.getExpiresAt(), authorizedClient.getScopes());
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(authorizedClient.getRefreshToken(), authorizedClient.getRefreshIssuedAt(), authorizedClient.getRefreshExpiresAt());
        return new OAuth2AuthorizedClient(clientRegistration, authorizedClient.getPrincipalUserId(), accessToken, refreshToken);
    }

    public static AuthorizedClient convertOAuth2AuthorizedClientToAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();
        AuthorizedClient authClientEntity = new AuthorizedClient(
                "",
                accessToken.getTokenValue(),
                authorizedClient.getClientRegistration().getRegistrationId(),
                principal.getName(),
                accessToken.getIssuedAt(),
                accessToken.getExpiresAt(),
                accessToken.getScopes()
        );

        if (refreshToken != null) {
            authClientEntity.setRefreshToken(refreshToken.getTokenValue());
            authClientEntity.setRefreshIssuedAt(refreshToken.getIssuedAt());
            authClientEntity.setRefreshExpiresAt(refreshToken.getExpiresAt());
        }

        return authClientEntity;
    }
}
