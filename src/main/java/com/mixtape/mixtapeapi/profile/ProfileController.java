package com.mixtape.mixtapeapi.profile;

import com.mixtape.mixtapeapi.AbstractRestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController extends AbstractRestController {

    public ProfileController(ProfileService profileService) {
        super(profileService);
    }

    @PostMapping
    public Profile createNew(@RequestBody Profile newProfile) {
        return profileService.save(newProfile);
    }

    @GetMapping
    public List<Profile> search(@RequestParam("displayName") String searchDisplayName) {
        return profileService.searchProfilesByDisplayName(searchDisplayName);
    }

    @GetMapping("/{id}")
    public Profile getById(@PathVariable String id) {
        return resolveProfileOr404(id);
    }
}
