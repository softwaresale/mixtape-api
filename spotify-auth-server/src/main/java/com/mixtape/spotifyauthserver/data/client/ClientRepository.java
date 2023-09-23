package com.mixtape.spotifyauthserver.data.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Persists clients in the database.
 *
 * @author Charlie Sale
 * @since 0.0.1
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByClientId(String clientId);
}
