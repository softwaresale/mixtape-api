package com.mixtape.spotifyauthserver.data.authclient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * OAuth2AuthorizedClientRepository implementation responsible for storing and retrieving client authorizations
 * throughout the application. This repository is implemented because OAuth2 authorization server uses it under the
 * hood to persist these entities.
 *
 * @author Charlie Sale
 * @since 0.0.1
 */
@Component
public class JpaOAuth2AuthorizedClientRepository implements OAuth2AuthorizedClientRepository {
    private static final Logger logger = LoggerFactory.getLogger(JpaOAuth2AuthorizedClientRepository.class);

    private final JpaAuthorizedClientRepository authorizedClientRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public JpaOAuth2AuthorizedClientRepository(JpaAuthorizedClientRepository authorizedClientRepository, ClientRegistrationRepository clientRegistrationRepository) {
        this.authorizedClientRepository = authorizedClientRepository;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, Authentication principal, HttpServletRequest request) {
        logger.debug("Trying to load authorized client -- Client registration: {}, principal: {}", clientRegistrationId, principal);
        Optional<AuthorizedClient> authorizedClientOpt = authorizedClientRepository.findByClientRegistrationIdAndPrincipalUserId(clientRegistrationId, principal.getName());
        if (authorizedClientOpt.isEmpty()) {
            return null;
        }

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
        if (clientRegistration == null) {
            return null;
        }

        AuthorizedClient authorizedClient = authorizedClientOpt.get();
        return (T) AuthorizedClientUtils.convertAuthorizedClientToOAuth2AuthorizedClient(authorizedClient, clientRegistration);
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal, HttpServletRequest request, HttpServletResponse response) {
        logger.debug("Saving authorized client -- AuthorizedClient: {}, principal: {}", authorizedClient, principal);
        AuthorizedClient authClientEntity = AuthorizedClientUtils.convertOAuth2AuthorizedClientToAuthorizedClient(authorizedClient, principal);
        authorizedClientRepository.save(authClientEntity);
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, Authentication principal, HttpServletRequest request, HttpServletResponse response) {
        logger.debug("Removing authorized client -- Client Registration Id: {}, principal: {}", clientRegistrationId, principal);
        authorizedClientRepository.removeByClientRegistrationIdAndPrincipalUserId(clientRegistrationId, principal.getName());
    }
}
