package com.mixtape.spotifyauthserver.config;

import com.mixtape.spotifyauthserver.federation.FederatedIdentityAuthenticationSuccessHandler;
import com.mixtape.spotifyauthserver.federation.JpaOAuth2UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * Just does basic security configurations. This protects endpoints and such
 *
 * @author Charlie Sale
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, JpaOAuth2UserHandler jpaOAuth2UserHandler) throws Exception {
        http
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/assets/**", "/webjars/**", "/login/**").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login")
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .loginPage("/login")
                                .successHandler(authenticationSuccessHandler(jpaOAuth2UserHandler))
                );

        return http.build();
    }

    /**
     * Creates a new authentication success handler that creates new users upon successful login. See
     * {@link FederatedIdentityAuthenticationSuccessHandler} for more details
     * @return AuthenticationSuccessHandler bean
     */
    private AuthenticationSuccessHandler authenticationSuccessHandler(JpaOAuth2UserHandler jpaOAuth2UserHandler) {
        FederatedIdentityAuthenticationSuccessHandler handler = new FederatedIdentityAuthenticationSuccessHandler();
        handler.setOAuth2UserHandler(jpaOAuth2UserHandler);
        return handler;
    }

    /**
     * Manages the session for saving codes and such across requests
     * @return Session registry
     */
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}


