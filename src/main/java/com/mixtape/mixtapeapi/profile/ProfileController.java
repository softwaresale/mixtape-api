package com.mixtape.mixtapeapi.profile;

import com.mixtape.mixtapeapi.AbstractRestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController extends AbstractRestController {

    public ProfileController(ProfileService profileService) {
        super(profileService);
    }

    @PostMapping
    public Profile createNewProfile(@RequestBody Profile newProfile) {
        return profileService.saveProfile(newProfile);
    }

    @GetMapping("/{profileId}")
    public Profile getProfile(@PathVariable String profileId) {
        return resolveProfileOr404(profileId);
    }
}
