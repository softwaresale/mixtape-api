package com.mixtape.spotifyauthserver.federation;

// tag::imports[]
import java.util.*;

import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import com.mixtape.spotifyauthserver.data.authclient.ProviderTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.web.server.ResponseStatusException;
// end::imports[]

/**
 * An {@link OAuth2TokenCustomizer} to map claims from a federated identity to
 * the {@code id_token} produced by this authorization server. It also tacks on
 * provider tokens for the current oauth2 access token if it's available and
 * adds it as a claim.
 *
 * @author Steve Riesenberg
 * @author Charlie Sale
 * @since 1.1
 */
// tag::class[]
public final class FederatedIdentityIdTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    private static final Logger logger = LoggerFactory.getLogger(FederatedIdentityIdTokenCustomizer.class);

    private static final Set<String> ID_TOKEN_CLAIMS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            IdTokenClaimNames.ISS,
            IdTokenClaimNames.SUB,
            IdTokenClaimNames.AUD,
            IdTokenClaimNames.EXP,
            IdTokenClaimNames.IAT,
            IdTokenClaimNames.AUTH_TIME,
            IdTokenClaimNames.NONCE,
            IdTokenClaimNames.ACR,
            IdTokenClaimNames.AMR,
            IdTokenClaimNames.AZP,
            IdTokenClaimNames.AT_HASH,
            IdTokenClaimNames.C_HASH
    )));

    private final ProviderTokenService providerTokenService;
    private final ProfileService profileService;

    public FederatedIdentityIdTokenCustomizer(ProviderTokenService providerTokenService, ProfileService profileService) {
        this.providerTokenService = providerTokenService;
        this.profileService = profileService;
    }

    @Override
    public void customize(JwtEncodingContext context) {
        logger.debug("Starting to customize token...");
        if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
            logger.debug("Customizing OpenID token");
            Map<String, Object> thirdPartyClaims = extractClaims(context.getPrincipal());
            context.getClaims().claims(existingClaims -> {
                // Remove conflicting claims set by this authorization server
                existingClaims.keySet().forEach(thirdPartyClaims::remove);

                // Remove standard id_token claims that could cause problems with clients
                ID_TOKEN_CLAIMS.forEach(thirdPartyClaims::remove);

                // Add all other claims directly to id_token
                existingClaims.putAll(thirdPartyClaims);
            });
        } else if (OAuth2ParameterNames.ACCESS_TOKEN.equals(context.getTokenType().getValue())) {
            logger.debug("Customizing OAuth2 Access token");
            extractProviderTokenValue(context.getAuthorization())
                    .ifPresent(providerTokenValue -> context.getClaims().claim("provider_token", providerTokenValue));
            swapSubForProfileId(context);
        }
    }

    private Map<String, Object> extractClaims(Authentication principal) {
        Map<String, Object> claims;
        if (principal.getPrincipal() instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) principal.getPrincipal();
            OidcIdToken idToken = oidcUser.getIdToken();
            claims = idToken.getClaims();
        } else if (principal.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) principal.getPrincipal();
            claims = oauth2User.getAttributes();
        } else {
            claims = Collections.emptyMap();
        }

        return new HashMap<>(claims);
    }

    private Optional<String> extractProviderTokenValue(OAuth2Authorization authorization) {
        if (authorization == null) {
            logger.warn("Could not extract provider token -- authorization was null");
            return Optional.empty();
        }

        // Try fetching a provider token for the current user
        return providerTokenService.getProviderTokenForUser(authorization);
    }

    private void swapSubForProfileId(JwtEncodingContext context) {
        // Get the sub claim
        String spotifyId = context.getPrincipal().getName();
        String profileId = getProfileIdForSpotifyId(spotifyId);
        context.getClaims()
                .subject(profileId)
                .claim("provider_id", spotifyId);
    }

    private String getProfileIdForSpotifyId(String spotifyId) throws ResponseStatusException {
        Profile existingProfile = this.profileService.findProfileBySpotifyId(spotifyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "While setting sub to local profile, no profile found with given spotify id"));
        return existingProfile.getId();
    }
}
// end::class[]
