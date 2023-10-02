package com.mixtape.mixtapeapi.profile;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    public Profile createNew(@RequestBody Profile newProfile) {
        return profileService.save(newProfile);
    }

    @GetMapping("/me")
    public Profile getByCurrentUser(Principal currentPrincipal) {
        var userAuthentication = (JwtAuthenticationToken) currentPrincipal;
        String id = userAuthentication.getName();
        return profileService.findProfile(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile could not be found"));
    }

    @GetMapping("/{id}")
    public Profile getById(@PathVariable String id) {
        return profileService.findProfile(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
