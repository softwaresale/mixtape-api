package com.mixtape.spotifyauthserver.data.authclient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Just persists {@link AuthorizedClient} models in the db
 *
 * @author Charlie Sale
 * @since 0.0.1
 */
@Repository
public interface JpaAuthorizedClientRepository extends JpaRepository<AuthorizedClient, String> {
    Optional<AuthorizedClient> findByClientRegistrationIdAndPrincipalUserId(String clientRegistrationId, String principalUserId);

    @Modifying
    void removeByClientRegistrationIdAndPrincipalUserId(String clientRegistrationId, String principalUserId);
}
