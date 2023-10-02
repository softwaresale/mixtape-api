package com.mixtape.spotifyauthserver.data.authclient;

import com.mixtape.spotifyauthserver.MockAuthentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.xml.stream.events.ProcessingInstruction;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProviderTokenServiceTest {

    static final String MOCK_CLIENT_REGISTRATION_ID = "client-id";
    static final String MOCK_PRINCIPAL_NAME = "user";
    static final String MOCK_ACCESS_TOKEN_VALUE = "access_token_value";
    static final String MOCK_REFRESH_TOKEN_VALUE = "refresh_token_value";
    public static final String MOCK_CLIENT_SECRET = "client_secret";
    public static final String MOCK_CLIENT_IDP_ID = "client_idp_id";
    public static final String MOCK_CLIENT_IDP_SECRET = "mock_client_idp_secret";
    public static final String MOCK_TOKEN_URI = "token://uri";
    public static final String MOCK_REFRESHED_ACCESS_TOKEN = "refreshed-access-token";
    OAuth2AccessToken mockAccessToken;
    OAuth2RefreshToken mockRefreshToken;
    OAuth2AuthenticationToken mockAuthenticationToken;
    RegisteredClient mockRegisteredClient;
    ClientRegistration mockClientRegistration;
    OAuth2Authorization mockAuthorization;
    OAuth2AuthorizedClient mockAuthorizedClient;
    AuthorizedClient mockAuthorizedClientEntity;

    @Mock
    JpaAuthorizedClientService mockAuthorizedClientService;

    @Mock
    OAuth2AuthorizedClientService mockOAuth2AuthorizedClientService;

    @Mock
    ClientRegistrationRepository mockClientRegistrationRepository;

    @Mock
    RestTemplate mockRestTemplate;

    ProviderTokenService service;

    @BeforeEach
    void setUp() {
        service = new ProviderTokenService(mockAuthorizedClientService, mockOAuth2AuthorizedClientService, mockClientRegistrationRepository, mockRestTemplate);
        mockRegisteredClient = RegisteredClient.withId(MOCK_CLIENT_REGISTRATION_ID)
                .clientId(MOCK_CLIENT_REGISTRATION_ID)
                .clientSecret(MOCK_CLIENT_SECRET)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("redirect://uri")
                .build();
        mockClientRegistration = ClientRegistration.withRegistrationId(MOCK_CLIENT_IDP_ID)
                .tokenUri(MOCK_TOKEN_URI)
                .authorizationUri("authorization://auth")
                .redirectUri("redirect://uri")
                .clientId(MOCK_CLIENT_IDP_ID)
                .clientSecret(MOCK_CLIENT_IDP_SECRET)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();
        mockAccessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                MOCK_ACCESS_TOKEN_VALUE,
                Instant.now().minus(Duration.ofMinutes(60)), // mock token was issued an hour ago
                Instant.now().plus(Duration.ofHours(1)) // and expires in an hour
        );
        mockRefreshToken = new OAuth2RefreshToken(
                MOCK_REFRESH_TOKEN_VALUE,
                Instant.now().minus(Duration.ofMinutes(60)),
                null // has no expiry
        );
        mockAuthenticationToken = new OAuth2AuthenticationToken(
                mock(OAuth2User.class), // make this not NPE
                null,
                MOCK_CLIENT_IDP_ID
        );
        mockAuthorization = OAuth2Authorization.withRegisteredClient(mockRegisteredClient)
                .accessToken(mockAccessToken)
                .refreshToken(mockRefreshToken)
                .attribute(java.security.Principal.class.getName(), mockAuthenticationToken)
                .principalName(MOCK_PRINCIPAL_NAME)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();

        mockAuthorizedClient = new OAuth2AuthorizedClient(
                mockClientRegistration,
                MOCK_PRINCIPAL_NAME,
                mockAccessToken,
                mockRefreshToken
        );
    }

    @Test
    void getProviderTokenForUser_success_whenTokenExistsAndIsValid() {
        when(mockOAuth2AuthorizedClientService.loadAuthorizedClient(MOCK_CLIENT_IDP_ID, MOCK_PRINCIPAL_NAME))
                .thenReturn(mockAuthorizedClient);

        Optional<String> providerToken = service.getProviderTokenForUser(mockAuthorization);

        assertThat(providerToken).hasValue(MOCK_ACCESS_TOKEN_VALUE);
        verify(mockOAuth2AuthorizedClientService).loadAuthorizedClient(MOCK_CLIENT_IDP_ID, MOCK_PRINCIPAL_NAME);
        verify(mockAuthorizedClientService, times(0)).getMostRecentExclusive(MOCK_CLIENT_IDP_ID, MOCK_PRINCIPAL_NAME);
    }

    @Test
    void getProviderToken_success_whenUpdatesExpiredBearerToken() {
        mockAccessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                MOCK_ACCESS_TOKEN_VALUE,
                Instant.now().minus(Duration.ofMinutes(60)), // mock token was issued an hour ago
                Instant.now().minus(Duration.ofHours(1)) // and expired an hour ago
        );
        mockAuthorization = OAuth2Authorization.withRegisteredClient(mockRegisteredClient)
                .accessToken(mockAccessToken)
                .refreshToken(mockRefreshToken)
                .attribute(java.security.Principal.class.getName(), mockAuthenticationToken)
                .principalName(MOCK_PRINCIPAL_NAME)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();

        mockAuthorizedClient = new OAuth2AuthorizedClient(
                mockClientRegistration,
                MOCK_PRINCIPAL_NAME,
                mockAccessToken,
                mockRefreshToken
        );

        mockAuthorizedClientEntity = AuthorizedClientUtils
                .convertOAuth2AuthorizedClientToAuthorizedClient(mockAuthorizedClient, new MockAuthentication(MOCK_PRINCIPAL_NAME));

        var refreshResponse = new ProviderTokenService.RefreshSpotifyTokenResponse();
        refreshResponse.setAccessToken(MOCK_REFRESHED_ACCESS_TOKEN);
        refreshResponse.setExpiresIn(3600);

        when(mockOAuth2AuthorizedClientService.loadAuthorizedClient(MOCK_CLIENT_IDP_ID, MOCK_PRINCIPAL_NAME))
                .thenReturn(mockAuthorizedClient);
        when(mockAuthorizedClientService.getMostRecentExclusive(MOCK_CLIENT_IDP_ID, MOCK_PRINCIPAL_NAME))
                .thenReturn(Optional.of(mockAuthorizedClientEntity));
        when(mockClientRegistrationRepository.findByRegistrationId(MOCK_CLIENT_IDP_ID))
                .thenReturn(mockClientRegistration);
        when(mockRestTemplate.postForEntity(eq(MOCK_TOKEN_URI), any(), eq(ProviderTokenService.RefreshSpotifyTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(refreshResponse, HttpStatus.OK));
        when(mockAuthorizedClientService.saveExclusive(any(AuthorizedClient.class)))
                .then(answer -> answer.getArguments()[0]); // return the called value

        Optional<String> providerToken = service.getProviderTokenForUser(mockAuthorization);

        assertThat(providerToken).hasValue(MOCK_REFRESHED_ACCESS_TOKEN);
        verify(mockOAuth2AuthorizedClientService).loadAuthorizedClient(MOCK_CLIENT_IDP_ID, MOCK_PRINCIPAL_NAME);
        verify(mockAuthorizedClientService).getMostRecentExclusive(MOCK_CLIENT_IDP_ID, MOCK_PRINCIPAL_NAME);
        verify(mockClientRegistrationRepository).findByRegistrationId(MOCK_CLIENT_IDP_ID);
        verify(mockAuthorizedClientService).saveExclusive(any(AuthorizedClient.class));
        verify(mockRestTemplate).postForEntity(
                MOCK_TOKEN_URI,
                Map.of(
                        OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.REFRESH_TOKEN.getValue(),
                        OAuth2ParameterNames.REFRESH_TOKEN, MOCK_REFRESH_TOKEN_VALUE
                ),
                ProviderTokenService.RefreshSpotifyTokenResponse.class
        );
    }

    @Test
    void getProviderToken_returnsEmpty_whenTheresNoRefreshToken() {
        mockAccessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                MOCK_ACCESS_TOKEN_VALUE,
                Instant.now().minus(Duration.ofMinutes(60)), // mock token was issued an hour ago
                Instant.now().minus(Duration.ofHours(1)) // and expired an hour ago
        );
        mockAuthorization = OAuth2Authorization.withRegisteredClient(mockRegisteredClient)
                .accessToken(mockAccessToken)
                .attribute(java.security.Principal.class.getName(), mockAuthenticationToken)
                .principalName(MOCK_PRINCIPAL_NAME)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();

        mockAuthorizedClient = new OAuth2AuthorizedClient(
                mockClientRegistration,
                MOCK_PRINCIPAL_NAME,
                mockAccessToken
        );

        mockAuthorizedClientEntity = AuthorizedClientUtils
                .convertOAuth2AuthorizedClientToAuthorizedClient(mockAuthorizedClient, new MockAuthentication(MOCK_PRINCIPAL_NAME));

        when(mockOAuth2AuthorizedClientService.loadAuthorizedClient(MOCK_CLIENT_IDP_ID, MOCK_PRINCIPAL_NAME))
                .thenReturn(mockAuthorizedClient);

        Optional<String> providerToken = service.getProviderTokenForUser(mockAuthorization);
        assertThat(providerToken).isEmpty();
    }

    @Test
    void getProviderToken_throws500_whenRefreshTokenExpires() {
        mockRefreshToken = new OAuth2RefreshToken(
                MOCK_REFRESH_TOKEN_VALUE,
                Instant.now().minus(Duration.ofMinutes(60)),
                Instant.now().minus(Duration.ofMinutes(30)) // expired...
        );
        mockAuthorization = OAuth2Authorization.withRegisteredClient(mockRegisteredClient)
                .accessToken(mockAccessToken)
                .refreshToken(mockRefreshToken)
                .attribute(java.security.Principal.class.getName(), mockAuthenticationToken)
                .principalName(MOCK_PRINCIPAL_NAME)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();

        mockAuthorizedClient = new OAuth2AuthorizedClient(
                mockClientRegistration,
                MOCK_PRINCIPAL_NAME,
                mockAccessToken,
                mockRefreshToken
        );

        mockAuthorizedClientEntity = AuthorizedClientUtils
                .convertOAuth2AuthorizedClientToAuthorizedClient(mockAuthorizedClient, new MockAuthentication(MOCK_PRINCIPAL_NAME));

        when(mockOAuth2AuthorizedClientService.loadAuthorizedClient(MOCK_CLIENT_IDP_ID, MOCK_PRINCIPAL_NAME))
                .thenReturn(mockAuthorizedClient);

        var thrownException = assertThrows(ResponseStatusException.class, () -> {
            service.getProviderTokenForUser(mockAuthorization);
        });
        assertThat(thrownException.getStatusCode()).isEqualTo(HttpStatus.NOT_IMPLEMENTED);
    }

    @Test
    void getProviderToken_throws500_whenRefreshAttemptFails() {
        mockAccessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                MOCK_ACCESS_TOKEN_VALUE,
                Instant.now().minus(Duration.ofMinutes(60)), // mock token was issued an hour ago
                Instant.now().minus(Duration.ofHours(1)) // and expired an hour ago
        );
        mockAuthorization = OAuth2Authorization.withRegisteredClient(mockRegisteredClient)
                .accessToken(mockAccessToken)
                .refreshToken(mockRefreshToken)
                .attribute(java.security.Principal.class.getName(), mockAuthenticationToken)
                .principalName(MOCK_PRINCIPAL_NAME)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();

        mockAuthorizedClient = new OAuth2AuthorizedClient(
                mockClientRegistration,
                MOCK_PRINCIPAL_NAME,
                mockAccessToken,
                mockRefreshToken
        );

        mockAuthorizedClientEntity = AuthorizedClientUtils
                .convertOAuth2AuthorizedClientToAuthorizedClient(mockAuthorizedClient, new MockAuthentication(MOCK_PRINCIPAL_NAME));

        var refreshResponse = new ProviderTokenService.RefreshSpotifyTokenResponse();
        refreshResponse.setAccessToken(MOCK_REFRESHED_ACCESS_TOKEN);
        refreshResponse.setExpiresIn(3600);

        when(mockOAuth2AuthorizedClientService.loadAuthorizedClient(MOCK_CLIENT_IDP_ID, MOCK_PRINCIPAL_NAME))
                .thenReturn(mockAuthorizedClient);
        when(mockAuthorizedClientService.getMostRecentExclusive(MOCK_CLIENT_IDP_ID, MOCK_PRINCIPAL_NAME))
                .thenReturn(Optional.of(mockAuthorizedClientEntity));
        when(mockClientRegistrationRepository.findByRegistrationId(MOCK_CLIENT_IDP_ID))
                .thenReturn(mockClientRegistration);
        when(mockRestTemplate.postForEntity(eq(MOCK_TOKEN_URI), any(), eq(ProviderTokenService.RefreshSpotifyTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(refreshResponse, HttpStatus.BAD_REQUEST));

        var throwException = assertThrows(ResponseStatusException.class, () -> {
            service.getProviderTokenForUser(mockAuthorization);
        });

        assertThat(throwException.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(throwException.getMessage()).isEqualTo(String.format("500 INTERNAL_SERVER_ERROR \"Got error while trying to refresh spotify token: %s\"", HttpStatus.BAD_REQUEST));
        verify(mockOAuth2AuthorizedClientService).loadAuthorizedClient(MOCK_CLIENT_IDP_ID, MOCK_PRINCIPAL_NAME);
        verify(mockAuthorizedClientService).getMostRecentExclusive(MOCK_CLIENT_IDP_ID, MOCK_PRINCIPAL_NAME);
        verify(mockClientRegistrationRepository).findByRegistrationId(MOCK_CLIENT_IDP_ID);
        verify(mockAuthorizedClientService, times(0)).saveExclusive(any(AuthorizedClient.class));
        verify(mockRestTemplate).postForEntity(
                MOCK_TOKEN_URI,
                Map.of(
                        OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.REFRESH_TOKEN.getValue(),
                        OAuth2ParameterNames.REFRESH_TOKEN, MOCK_REFRESH_TOKEN_VALUE
                ),
                ProviderTokenService.RefreshSpotifyTokenResponse.class
        );
    }
}