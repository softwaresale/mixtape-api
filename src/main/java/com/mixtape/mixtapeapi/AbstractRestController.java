package com.mixtape.mixtapeapi;

import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Provides basic utilities that would be useful in all controllers. The primary things it provides is
 * - resolving user ids to either the currently authenticated user or some provided user
 * - getting provider tokens as useful
 */
public abstract class AbstractRestController {

    protected final ProfileService profileService;

    protected AbstractRestController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * Resolve and fetch the requested user id. If it's me, then it gets the currently authenticated user. Otherwise,
     * it gets the user with the requested id
     * @param requestedId The id to request
     * @return The profile, if it exists
     */
    public Optional<Profile> resolveProfile(String requestedId) {
        // if the id is "me", then use the current authenticated user
        String profileId = requestedId;
        if ("me".equals(profileId)) {
            // It's some requested id
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            profileId = authentication.getName();
        }

        return profileService.findProfile(profileId);
    }

    /**
     * Resolves the profile and throws a 404 exception if the profile is empty
     * @param requestedId The id to request
     * @return The requested profile
     */
    public Profile resolveProfileOr404(String requestedId) {
        return resolveProfile(requestedId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Profile with id %s does not exist", requestedId)));
    }

    /**
     * Gets the provider token of the currently authenticated user.
     * @return The provider token, or empty if unauthenticated or not found
     */
    public Optional<String> getProviderToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }

        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
            return Optional.of(token)
                    .map(JwtAuthenticationToken::getToken)
                    .map(jwt -> jwt.getClaim("provider_token"));
        } else {
            return Optional.empty();
        }
    }
}
