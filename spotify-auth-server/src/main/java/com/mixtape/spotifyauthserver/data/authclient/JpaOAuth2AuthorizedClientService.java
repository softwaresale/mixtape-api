package com.mixtape.spotifyauthserver.data.authclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * OAuth2AuthorizedClient implementation that stores authorized clients in the database. Behaves very similarly to
 * {@link JpaOAuth2AuthorizedClientRepository}, but is easier to work with in the application domain layer (no requests,
 * more ids, etc). Prefer to use this over {@link JpaOAuth2AuthorizedClientRepository} unless you have a really good
 * reason to.
 *
 * @author Charlie Sale
 * @since 0.0.1
 */
@Service
public class JpaOAuth2AuthorizedClientService implements OAuth2AuthorizedClientService {

    private final static Logger logger = LoggerFactory.getLogger(JpaOAuth2AuthorizedClientService.class);

    private final JpaAuthorizedClientRepository authorizedClientRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public JpaOAuth2AuthorizedClientService(JpaAuthorizedClientRepository authorizedClientRepository,
                                            ClientRegistrationRepository clientRegistrationRepository) {
        this.authorizedClientRepository = authorizedClientRepository;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
        logger.debug("loading authorized client: client registration {}, principal id {}", clientRegistrationId, principalName);

        Optional<AuthorizedClient> authorizedClient = authorizedClientRepository
                .findByClientRegistrationIdAndPrincipalUserId(clientRegistrationId, principalName);
        if (authorizedClient.isEmpty()) {
            return null;
        }

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
        if (clientRegistration == null) {
            return null;
        }


        // TODO deal with this cast...
        return (T) AuthorizedClientUtils
                .convertAuthorizedClientToOAuth2AuthorizedClient(authorizedClient.get(), clientRegistration);
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        logger.debug("saving new authorized client: client registration {}, principal id {}", authorizedClient.getClientRegistration().getClientId(), principal.getName());
        AuthorizedClient authClientEntity = AuthorizedClientUtils
                .convertOAuth2AuthorizedClientToAuthorizedClient(authorizedClient, principal);

        this.authorizedClientRepository.save(authClientEntity);
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        logger.debug("removing authorized client: client registration {}, principal id {}", clientRegistrationId, principalName);
        this.authorizedClientRepository.removeByClientRegistrationIdAndPrincipalUserId(clientRegistrationId, principalName);
    }
}
