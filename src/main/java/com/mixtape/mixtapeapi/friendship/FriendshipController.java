package com.mixtape.mixtapeapi.friendship;

import com.mixtape.mixtapeapi.AbstractRestController;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile/{profileId}/friendship")
public class FriendshipController extends AbstractRestController {
    private final FriendshipService friendshipService;

    public FriendshipController(ProfileService profileService, FriendshipService friendshipService) {
        super(profileService);
        this.friendshipService = friendshipService;
    }

    @GetMapping("/friends")
    public List<Profile> getFriendsForProfile(@PathVariable String profileId) {
        Profile profile = resolveProfileOr404(profileId);
        return friendshipService.findFriendsForProfile(profile);
    }

    @GetMapping("/{id}")
    public Friendship getById(@PathVariable String id) {
        return friendshipService.findFriendship(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
