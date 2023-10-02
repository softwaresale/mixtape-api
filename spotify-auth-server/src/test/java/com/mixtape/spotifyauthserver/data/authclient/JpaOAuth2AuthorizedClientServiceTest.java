package com.mixtape.spotifyauthserver.data.authclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaOAuth2AuthorizedClientServiceTest {

    @Mock
    JpaAuthorizedClientService mockAuthorizedClientService;
    @Mock
    ClientRegistrationRepository mockClientRegistrationRepository;

    JpaOAuth2AuthorizedClientService service;

    @BeforeEach
    void setUp() {
        service = new JpaOAuth2AuthorizedClientService(mockAuthorizedClientService, mockClientRegistrationRepository);
    }

    @Test
    void loadAuthorizedClient_success_whenEntitiesExist() {
        String clientRegistrationId = "client-reg";
        String principalName = "prince";
        String providerTokenValue = "provider-token";
        AuthorizedClient mockClient = new AuthorizedClient();
        mockClient.setPrincipalUserId(principalName);
        mockClient.setProviderToken(providerTokenValue);
        mockClient.setRefreshToken("refresh_token_value");
        ClientRegistration mockClientRegistration = ClientRegistration.withRegistrationId("id")
                .clientId(clientRegistrationId)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("redirect://uri")
                .authorizationUri("authorization://uri")
                .tokenUri("token://uri")
                .build();
        when(mockAuthorizedClientService.getMostRecentExclusive(clientRegistrationId, principalName))
                .thenReturn(Optional.of(mockClient));
        when(mockClientRegistrationRepository.findByRegistrationId(clientRegistrationId))
                .thenReturn(mockClientRegistration);

        OAuth2AuthorizedClient client = service.loadAuthorizedClient(clientRegistrationId, principalName);

        assertThat(client).isNotNull();
        assertThat(client.getPrincipalName()).isEqualTo(principalName);
        assertThat(client.getAccessToken().getTokenValue()).isEqualTo(providerTokenValue);
        assertThat(client.getClientRegistration().getClientId()).isEqualTo(clientRegistrationId);
        verify(mockAuthorizedClientService).getMostRecentExclusive(clientRegistrationId, principalName);
        verify(mockClientRegistrationRepository).findByRegistrationId(clientRegistrationId);
    }

    @Test
    void loadAuthorizedClient_returnsNull_whenEntityDoesntExist() {
        String clientRegistrationId = "client-reg";
        String principalName = "prince";
        when(mockAuthorizedClientService.getMostRecentExclusive(clientRegistrationId, principalName))
                .thenReturn(Optional.empty());

        OAuth2AuthorizedClient client = service.loadAuthorizedClient(clientRegistrationId, principalName);

        assertThat(client).isNull();
        verify(mockAuthorizedClientService).getMostRecentExclusive(clientRegistrationId, principalName);
        verify(mockClientRegistrationRepository, times(0)).findByRegistrationId(any());
    }

    @Test
    void loadAuthorizedClient_returnsNull_whenClientRegistrationDoesntExist() {
        String clientRegistrationId = "client-reg";
        String principalName = "prince";
        String providerTokenValue = "provider-token";
        AuthorizedClient mockClient = new AuthorizedClient();
        mockClient.setProviderToken(providerTokenValue);
        when(mockAuthorizedClientService.getMostRecentExclusive(clientRegistrationId, principalName))
                .thenReturn(Optional.of(mockClient));
        when(mockClientRegistrationRepository.findByRegistrationId(clientRegistrationId))
                .thenReturn(null);

        OAuth2AuthorizedClient client = service.loadAuthorizedClient(clientRegistrationId, principalName);

        assertThat(client).isNull();
        verify(mockAuthorizedClientService).getMostRecentExclusive(clientRegistrationId, principalName);
        verify(mockClientRegistrationRepository).findByRegistrationId(clientRegistrationId);
    }
}