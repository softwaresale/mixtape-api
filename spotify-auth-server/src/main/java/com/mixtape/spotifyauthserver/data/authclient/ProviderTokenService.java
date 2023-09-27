package com.mixtape.spotifyauthserver.data.authclient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Fetches the provider token for a currently authenticated user. It looks up the provider token for the currently
 * authenticating user and retrieves it. It is possible that an authenticated user does not have a provider token.
 * This service is also responsible for trying to update the bearer token.
 *
 * @author Charlie Sale
 * @since 0.0.1
 */
@Service
public class ProviderTokenService {

    private final static Logger logger = LoggerFactory.getLogger(ProviderTokenService.class);

    private final JpaAuthorizedClientRepository jpaAuthorizedClientRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final RestTemplate defaultRestTemplate;

    public ProviderTokenService(JpaAuthorizedClientRepository jpaAuthorizedClientRepository,
                                OAuth2AuthorizedClientService authorizedClientService,
                                ClientRegistrationRepository clientRegistrationRepository,
                                RestTemplate defaultRestTemplate) {
        this.jpaAuthorizedClientRepository = jpaAuthorizedClientRepository;
        this.authorizedClientService = authorizedClientService;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.defaultRestTemplate = defaultRestTemplate;
    }

    /**
     * Try getting the provider token for the currently authenticated user. If the user doesn't have one, it returns
     * null. This method also checks if the bearer token is expired and tries to refresh it if a refresh token is
     * available. If the refresh fails, it'll throw an exception.
     * @param authorization The authorized user to refresh
     * @return An optional provider token, or empty if doesn't exist or couldn't be refreshed
     * @throws ResponseStatusException Throws a 500 exception if refreshing failed
     */
    public Optional<String> getProviderTokenForUser(OAuth2Authorization authorization) throws ResponseStatusException {

        var oauth2AuthToken = (OAuth2AuthenticationToken) authorization.getAttribute(java.security.Principal.class.getName());
        if (oauth2AuthToken == null) {
            logger.warn("authorization principal attribute does not hold an OAuth2AuthenticationToken. It was null");
            return Optional.empty();
        }

        String authorizedClientRegistrationId = oauth2AuthToken.getAuthorizedClientRegistrationId();
        String principalId = authorization.getPrincipalName();
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(authorizedClientRegistrationId, principalId);
        Optional<OAuth2AuthorizedClient> refreshedBearerToken = updateBearerToken(authorizedClient, authorizedClientRegistrationId);

        return refreshedBearerToken
                .map(OAuth2AuthorizedClient::getAccessToken)
                .map(OAuth2AccessToken::getTokenValue);
    }

    /**
     * Figures out if we need to update the bearer token. If we do, it performs the necessary requests. If it needs
     * refreshing but no refresh token is available, it returns an empty optional.
     * @param oAuth2AuthorizedClient The client we are trying to refresh
     * @param clientRegistrationId The id of the client that the authorized client is authorized under (lol)
     * @return Refreshed bearer token or nothing
     * @throws ResponseStatusException If there was an error while performing the refresh request
     */
    private Optional<OAuth2AuthorizedClient> updateBearerToken(OAuth2AuthorizedClient oAuth2AuthorizedClient, String clientRegistrationId) throws ResponseStatusException {

        boolean accessTokenNeedsRefreshing = Optional.ofNullable(oAuth2AuthorizedClient)
                .flatMap(client -> Optional.ofNullable(client.getAccessToken()))
                .map(OAuth2AccessToken::getExpiresAt)
                .map(expiresAt -> expiresAt.compareTo(Instant.now()) < 0)
                .orElse(false); // if there is no expiry, do nothing???

        boolean refreshTokenNeedsRefreshing = Optional.ofNullable(oAuth2AuthorizedClient)
                .flatMap(client -> Optional.ofNullable(client.getRefreshToken()))
                .flatMap(token -> Optional.ofNullable(token.getExpiresAt()))
                .map(expiresAt -> expiresAt.compareTo(Instant.now()) < 0)
                .orElse(false);

        // Check if the authorized client is expired
        if (accessTokenNeedsRefreshing) {

            // if the access token is expired but there is no refresh token, then we should just return an empty
            // bearer token. The alternative is throwing an exception, but that seems extreme...
            if (oAuth2AuthorizedClient.getRefreshToken() == null) {
                // TODO consider removing the record??
                return Optional.empty();
            }

            AuthorizedClient authorizedClient = mapOAuth2AuthorizedClientToEntity(oAuth2AuthorizedClient)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "OAuth2AuthorizedClient could not be mapped back to original entity"));

            ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
            if (clientRegistration == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("No client registration for %s", clientRegistrationId));
            }

            // if it is, we need to refresh it
            RefreshSpotifyTokenResponse refreshedTokenResponse = performRefreshRequest(authorizedClient, clientRegistration);
            authorizedClient.setProviderToken(refreshedTokenResponse.getAccessToken());
            authorizedClient.setExpiresAt(Instant.now().plus(Duration.ofSeconds(refreshedTokenResponse.getExpiresIn())));

            AuthorizedClient updateEntity = jpaAuthorizedClientRepository.save(authorizedClient);

            // Just use the original service. I know there's an unnecessary request here...
            return Optional.of(AuthorizedClientUtils.convertAuthorizedClientToOAuth2AuthorizedClient(updateEntity, clientRegistration));
        } else if (refreshTokenNeedsRefreshing) {
            throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Spotify refresh token expiration not implemented yet");
        } else {
            // there is nothing to do
            return Optional.of(oAuth2AuthorizedClient);
        }
    }

    private Optional<AuthorizedClient> mapOAuth2AuthorizedClientToEntity(OAuth2AuthorizedClient authorizedClient) {
        return jpaAuthorizedClientRepository.findByClientRegistrationIdAndPrincipalUserId(authorizedClient.getClientRegistration().getClientId(), authorizedClient.getPrincipalName());
    }

    /**
     * Actually do the refreshing. Performs a network request
     * @param authorizedClient The client to refresh
     * @param clientRegistration The registration that it's a part of
     * @return A non-null refresh token response
     * @throws ResponseStatusException If there was an exception while performing the request
     */
    @NonNull
    private RefreshSpotifyTokenResponse performRefreshRequest(AuthorizedClient authorizedClient, ClientRegistration clientRegistration) throws ResponseStatusException {
        Map<String, String> body = new HashMap<>();
        body.put(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.REFRESH_TOKEN.getValue());
        body.put(OAuth2ParameterNames.REFRESH_TOKEN, authorizedClient.getRefreshToken());
        RestTemplate refreshTokenExchangeTemplate = new RestTemplateBuilder()
                .basicAuthentication(clientRegistration.getClientId(), clientRegistration.getClientSecret())
                .configure(defaultRestTemplate);
        String tokenUri = clientRegistration.getProviderDetails().getTokenUri();

        ResponseEntity<RefreshSpotifyTokenResponse> response = refreshTokenExchangeTemplate
                .postForEntity(tokenUri, body, RefreshSpotifyTokenResponse.class);

        if (response.getStatusCode().isError()) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Got error while trying to refresh spotify token: %s", response.getStatusCode())
            );
        } else if (!response.hasBody()) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Refresh token response does not have a body"
            );
        }

        return Objects.requireNonNull(response.getBody());
    }

    protected static class RefreshSpotifyTokenResponse {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("scope")
        private Set<String> scope;

        @JsonProperty("expires_in")
        private Integer expiresIn;

        public RefreshSpotifyTokenResponse() {
        }

        public RefreshSpotifyTokenResponse(String accessToken, String tokenType, Set<String> scope, Integer expiresIn) {
            this.accessToken = accessToken;
            this.tokenType = tokenType;
            this.scope = scope;
            this.expiresIn = expiresIn;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public Set<String> getScope() {
            return scope;
        }

        public void setScope(Set<String> scope) {
            this.scope = scope;
        }

        @JsonSetter("scope")
        public void setScope(String spaceSeparated) {
            this.scope = Arrays.stream(spaceSeparated.split(" "))
                    .collect(Collectors.toSet());
        }

        public Integer getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(Integer expiresIn) {
            this.expiresIn = expiresIn;
        }
    }
}
