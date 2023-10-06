package com.mixtape.spotifyauthserver.data.authclient;

import com.mixtape.spotifyauthserver.data.SetCSVConverter;
import com.mixtape.spotifyauthserver.data.authorization.Authorization;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Set;

/**
 * Represents a provider token, which is a slightly different notion than {@link Authorization}.
 * This is the authorization that happens between spotify and our authentication server. The provider token is the
 * bearer token used to access Spotify's API. NOTE: each user should have about 1 provider token record. It should
 * be updated as time goes on
 *
 * @author Charlie Sale
 * @since 0.0.1
 */
@Entity
@Table(name="`authorized_client`")
public class AuthorizedClient {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String providerToken;
    private String clientRegistrationId;
    private String principalUserId;
    private Instant issuedAt;
    private Instant expiresAt;
    private String refreshToken;
    private Instant refreshIssuedAt;
    private Instant refreshExpiresAt;

    @Convert(converter = SetCSVConverter.class)
    private Set<String> scopes;

    public AuthorizedClient() {
    }

    public AuthorizedClient(String id, String providerToken, String clientRegistrationId, String principalUserId, Instant issuedAt, Instant expiresAt, Set<String> scopes) {
        this.id = id;
        this.providerToken = providerToken;
        this.clientRegistrationId = clientRegistrationId;
        this.principalUserId = principalUserId;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        // these may not be provided, so they need to be set manually
        this.refreshToken = null;
        this.refreshIssuedAt = null;
        this.refreshExpiresAt = null;
        this.scopes = scopes;
    }

    public AuthorizedClient(AuthorizedClient other) {
        this(other.id, other.providerToken, other.clientRegistrationId, other.principalUserId, other.issuedAt, other.expiresAt, other.scopes);
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProviderToken() {
        return providerToken;
    }

    public void setProviderToken(String providerToken) {
        this.providerToken = providerToken;
    }

    public String getClientRegistrationId() {
        return clientRegistrationId;
    }

    public void setClientRegistrationId(String clientRegistrationId) {
        this.clientRegistrationId = clientRegistrationId;
    }

    public String getPrincipalUserId() {
        return principalUserId;
    }

    public void setPrincipalUserId(String principalUserId) {
        this.principalUserId = principalUserId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Instant getRefreshIssuedAt() {
        return refreshIssuedAt;
    }

    public void setRefreshIssuedAt(Instant refreshIssuedAt) {
        this.refreshIssuedAt = refreshIssuedAt;
    }

    public Instant getRefreshExpiresAt() {
        return refreshExpiresAt;
    }

    public void setRefreshExpiresAt(Instant refreshExpiresAt) {
        this.refreshExpiresAt = refreshExpiresAt;
    }
}
