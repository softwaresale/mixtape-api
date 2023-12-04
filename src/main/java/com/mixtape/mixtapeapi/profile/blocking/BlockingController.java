package com.mixtape.mixtapeapi.profile.blocking;

import com.mixtape.mixtapeapi.AbstractRestController;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile/{profileId}")
public class BlockingController extends AbstractRestController {

    private final BlockedProfileService blockedProfileService;

    public BlockingController(ProfileService profileService, BlockedProfileService blockedProfileService) {
        super(profileService);
        this.blockedProfileService = blockedProfileService;
    }

    @GetMapping("/blocklist")
    public List<Profile> getBlockedProfiles(@PathVariable String profileId) {
        Profile blocker = resolveProfileOr404(profileId);
        return blockedProfileService.getBlockedProfiles(blocker);
    }

    @PostMapping("/blocklist/{blockedProfileId}")
    public boolean blockProfile(@PathVariable String profileId, @PathVariable String blockedProfileId) {
        Profile blocker = resolveProfileOr404(profileId);
        Profile blockee = resolveProfileOr404(blockedProfileId);
        return blockedProfileService.blockProfile(blocker, blockee);
    }

    @PutMapping("/blocklist/{blockedProfileId}/unblock")
    public boolean unblockProfile(@PathVariable String profileId, @PathVariable String blockedProfileId) {
        Profile blocker = resolveProfileOr404(profileId);
        Profile blockee = resolveProfileOr404(blockedProfileId);

        return blockedProfileService.unblockProfile(blocker, blockee);
    }
}
