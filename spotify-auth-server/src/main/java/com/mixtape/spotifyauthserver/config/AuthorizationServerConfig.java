package com.mixtape.spotifyauthserver.config;

import com.mixtape.spotifyauthserver.data.authclient.JpaOAuth2AuthorizedClientRepository;
import com.mixtape.spotifyauthserver.data.authclient.ProviderTokenService;
import com.mixtape.spotifyauthserver.data.authorization.AuthorizationRepository;
import com.mixtape.spotifyauthserver.data.authorization.JpaOAuth2AuthorizationService;
import com.mixtape.spotifyauthserver.data.client.ClientRepository;
import com.mixtape.spotifyauthserver.data.client.JpaRegisteredClientRepository;
import com.mixtape.spotifyauthserver.data.consent.AuthorizationConsentRepository;
import com.mixtape.spotifyauthserver.data.consent.JpaOAuth2AuthorizationConsentService;
import com.mixtape.spotifyauthserver.federation.FederatedIdentityIdTokenCustomizer;
import com.mixtape.spotifyauthserver.jwks.Jwks;
import com.mixtape.spotifyauthserver.data.authorization.Authorization;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.util.UUID;

/**
 *
 * This file class configures just the authorization server and andpoints related ot it
 *
 * @author Joe Grandja
 * @author Daniel Garnier-Moiroux
 * @author Steve Riesenberg
 * @author Charlie Sale
 * @since 1.1
 */
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {
    private static final String CUSTOM_CONSENT_PAGE_URI = "/oauth2/consent";

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http, RegisteredClientRepository registeredClientRepository,
            OAuth2AuthorizationService jpaOAuthAuthorizationService,
            JpaOAuth2AuthorizedClientRepository authorizedClientRepository) throws Exception {

        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .registeredClientRepository(registeredClientRepository)
                .authorizationService(jpaOAuthAuthorizationService)
                .authorizationEndpoint(authorizationEndpoint ->
                        authorizationEndpoint
                                .consentPage(CUSTOM_CONSENT_PAGE_URI)
                )
                .oidc(Customizer.withDefaults());	// Enable OpenID Connect 1.0

        http
                .oauth2Client(client -> client
                        .authorizedClientRepository(authorizedClientRepository)
                )
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                )
                .oauth2ResourceServer(oauth2ResourceServer ->
                        oauth2ResourceServer.jwt(Customizer.withDefaults()));
        return http.build();
    }

    /**
     * Creates a registered client repository with a single client registered. See {@link ClientRepository} for
     * more details
     * @param clientRepository The JPA repo to store registered clients
     * @return Registered client repository bean
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(ClientRepository clientRepository) {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("spotify-client")
                .clientSecret("{noop}secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri("http://127.0.0.1:8081/login/oauth2/code/spotify-client")
                .redirectUri("http://127.0.0.1:8081/authorized")
                .postLogoutRedirectUri("http://127.0.0.1:8081/logged-out")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();

        // Save registered client's in db as if in-memory
        JpaRegisteredClientRepository registeredClientRepository = new JpaRegisteredClientRepository(clientRepository);
        registeredClientRepository.save(registeredClient);

        return registeredClientRepository;
    }

    /**
     * Provides an oauth2 authorization service. See {@link JpaOAuth2AuthorizationService} for more details
     * @param authorizationRepository jpa repository for persisting and retriving {@link Authorization}
     *                                records
     * @param registeredClientRepository Bean for registered clients
     * @return OAuth2AuthorizationService
     */
    @Bean
    public OAuth2AuthorizationService authorizationService(AuthorizationRepository authorizationRepository,
                                                           RegisteredClientRepository registeredClientRepository) {

        return new JpaOAuth2AuthorizationService(authorizationRepository, registeredClientRepository);
    }

    /**
     * Provides an oauth2 consent service. See {@link JpaOAuth2AuthorizationConsentService} for more details
     * @param authConsentRepo JPA Repository of authorization consent records
     * @param registeredClientRepository Bean for Registered client repo
     * @return OAuth2AuthorizedConsentSErvice
     */
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(AuthorizationConsentRepository authConsentRepo,
                                                                         RegisteredClientRepository registeredClientRepository) {
        // Will be used by the ConsentController
        return new JpaOAuth2AuthorizationConsentService(authConsentRepo, registeredClientRepository);
    }

    /**
     * Customizes each access token that gets created. See {@link FederatedIdentityIdTokenCustomizer} for more details.
     * @param providerTokenService Gets the bearer token for the current user
     * @return token customizer
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> idTokenCustomizer(ProviderTokenService providerTokenService) {
        return new FederatedIdentityIdTokenCustomizer(providerTokenService);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = Jwks.generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * Use this to customize the authorization service endpoints
     * @return AuthorizationServerSettings bean
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .build();
    }
}