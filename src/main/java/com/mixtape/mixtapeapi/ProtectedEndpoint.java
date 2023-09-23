package com.mixtape.mixtapeapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/protected")
public class ProtectedEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(ProtectedEndpoint.class);

    @GetMapping
    public String protectedEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("Got authentication: {}", authentication);
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
            Jwt jwt = token.getToken();
            String jwtValue = jwt.getTokenValue();
            logger.debug("Specifically, got JWT value: {}", jwtValue);
            logger.debug("jwt inst: {}", jwt);

            String providerToken = jwt.getClaim("provider_token");
            logger.info("Got provider token: {}", providerToken);
        }
        return "Hi!";
    }
}
