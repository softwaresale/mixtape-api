package com.mixtape.spotifyauthserver.data.authclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JpaAuthorizedClientService {

    private static final Logger logger = LoggerFactory.getLogger(JpaAuthorizedClientService.class);

    private final JpaAuthorizedClientRepository repository;

    public JpaAuthorizedClientService(JpaAuthorizedClientRepository jpaAuthorizedClientRepository) {
        this.repository = jpaAuthorizedClientRepository;
    }

    public Optional<AuthorizedClient> getMostRecentExclusive(String clientRegistrationId, String principalId) {
        // This is a list of all authorized clients that match the given info ordered from most recently issued
        // (most recently issued is at the front)
        List<AuthorizedClient> authorizedClients = repository
                .findAllByClientRegistrationIdAndPrincipalUserIdOrderByIssuedAtDesc(clientRegistrationId, principalId);

        if (authorizedClients.isEmpty()) {
            logger.debug("No recent authorized client for {}, user {}", clientRegistrationId, principalId);
            return Optional.empty();
        }

        AuthorizedClient mostRecentAuthorizedClient = authorizedClients.remove(0);
        if (!authorizedClients.isEmpty()) {
            // Delete any remaining authorized clients. There should only be one in existence at a time
            logger.debug("Removing outdated authorized clients for spotify user {}: {}", principalId, authorizedClients);
            repository.deleteAll(authorizedClients);
        }

        return Optional.of(mostRecentAuthorizedClient);
    }

    public AuthorizedClient saveExclusive(AuthorizedClient authorizedClient) {
        // find any other authorized clients with this spotify UID and client id
        String spotifyId = authorizedClient.getPrincipalUserId();
        String clientRegId = authorizedClient.getClientRegistrationId();

        List<AuthorizedClient> existing = repository
                .findAllByClientRegistrationIdAndPrincipalUserIdOrderByIssuedAtDesc(clientRegId, spotifyId);

        if  (!existing.isEmpty()) {
            logger.info("Deleting {} old authorized client(s) while saving a new one for {}, user {}", existing.size(), clientRegId, spotifyId);
            repository.deleteAll(existing);
        }

        return repository.save(authorizedClient);
    }

    public void remove(String clientRegId, String spotifyId) {
        logger.debug("Removing authorized client for {}, user {}", clientRegId, spotifyId);
        repository.removeByClientRegistrationIdAndPrincipalUserId(clientRegId, spotifyId);
    }
}
