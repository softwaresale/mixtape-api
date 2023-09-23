package com.mixtape.spotifyauthserver.data.authclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaOAuth2AuthorizedClientRepositoryTest {

    @Mock
    Authentication mockAuthentication;
    @Mock
    JpaAuthorizedClientRepository mockClientRepository;
    @Mock
    ClientRegistrationRepository mockClientRegistrationRepository;

    JpaOAuth2AuthorizedClientRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JpaOAuth2AuthorizedClientRepository(mockClientRepository, mockClientRegistrationRepository);
    }

    @Test
    void loadAuthorizedClient_success_whenModelsWork() {
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
        when(mockAuthentication.getName()).thenReturn(principalName);
        when(mockClientRepository.findByClientRegistrationIdAndPrincipalUserId(clientRegistrationId, principalName))
                .thenReturn(Optional.of(mockClient));
        when(mockClientRegistrationRepository.findByRegistrationId(clientRegistrationId))
                .thenReturn(mockClientRegistration);

        OAuth2AuthorizedClient client = repository.loadAuthorizedClient(clientRegistrationId, mockAuthentication, null);

        assertThat(client).isNotNull();
        assertThat(client.getPrincipalName()).isEqualTo(principalName);
        assertThat(client.getAccessToken().getTokenValue()).isEqualTo(providerTokenValue);
        assertThat(client.getClientRegistration().getClientId()).isEqualTo(clientRegistrationId);
        verify(mockClientRepository).findByClientRegistrationIdAndPrincipalUserId(clientRegistrationId, principalName);
        verify(mockClientRegistrationRepository).findByRegistrationId(clientRegistrationId);
    }

    @Test
    void loadAuthorizedClient_returnsNull_whenAuthorizedClientDoesntExist() {
        String clientRegistrationId = "client-reg";
        String principalName = "prince";
        when(mockAuthentication.getName()).thenReturn(principalName);
        when(mockClientRepository.findByClientRegistrationIdAndPrincipalUserId(clientRegistrationId, principalName))
                .thenReturn(Optional.empty());

        OAuth2AuthorizedClient client = repository.loadAuthorizedClient(clientRegistrationId, mockAuthentication, null);

        assertThat(client).isNull();
        verify(mockClientRepository).findByClientRegistrationIdAndPrincipalUserId(clientRegistrationId, principalName);
        verify(mockClientRegistrationRepository, times(0)).findByRegistrationId(clientRegistrationId);
    }

    @Test
    void loadAuthorizedClient_returnsNull_whenClientRegistrationDoesntExist() {
        String clientRegistrationId = "client-reg";
        String principalName = "prince";
        String providerTokenValue = "provider-token";
        AuthorizedClient mockClient = new AuthorizedClient();
        mockClient.setProviderToken(providerTokenValue);
        when(mockAuthentication.getName()).thenReturn(principalName);
        when(mockClientRepository.findByClientRegistrationIdAndPrincipalUserId(clientRegistrationId, principalName))
                .thenReturn(Optional.of(mockClient));
        when(mockClientRegistrationRepository.findByRegistrationId(clientRegistrationId))
                .thenReturn(null);

        OAuth2AuthorizedClient client = repository.loadAuthorizedClient(clientRegistrationId, mockAuthentication, null);

        assertThat(client).isNull();
        verify(mockClientRepository).findByClientRegistrationIdAndPrincipalUserId(clientRegistrationId, principalName);
        verify(mockClientRegistrationRepository).findByRegistrationId(clientRegistrationId);
    }
}