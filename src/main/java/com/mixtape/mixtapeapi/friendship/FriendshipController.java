package com.mixtape.mixtapeapi.friendship;

import com.mixtape.mixtapeapi.AbstractRestController;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    // it's important that these go here because friends might be shadowed/absorbed {friendshipId}
    @GetMapping("/friends")
    public List<Profile> getFriendsForProfile(@PathVariable String profileId) {
        Profile profile = resolveProfileOr404(profileId);
        return friendshipService.findFriendsForProfile(profile);
    }

    @DeleteMapping("/friends/{friendId}")
    public void deleteFriendshipWithFriend(@PathVariable String profileId, @PathVariable String friendId) {
        Profile deleter = resolveProfileOr404(profileId);
        Profile deletee = resolveProfileOr404(friendId);

        friendshipService.removeFriendshipByFriend(deleter, deletee);
    }

    @GetMapping("/{friendshipId}")
    public Friendship getFriendship(@PathVariable String friendshipId) {
        return friendshipService.findFriendship(friendshipId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Friendship createFriendship(@PathVariable String profileId, @RequestBody String requestedTargetID) {
        Profile initiator = resolveProfileOr404(profileId);
        Profile requestedTarget = resolveProfileOr404(requestedTargetID);
        return friendshipService.createFriendship(initiator, requestedTarget);
    }

    @PutMapping("/{friendshipId}/accept")
    public Friendship acceptFriendship(@PathVariable String profileId, @PathVariable String friendshipId) {
        Profile profile = resolveProfileOr404(profileId);
        return friendshipService.acceptFriendship(profile, friendshipId);
    }

    @DeleteMapping("/{friendshipId}/deny")
    public void denyFriendship(@PathVariable String profileId, @PathVariable String friendshipId) {
        Profile profile = resolveProfileOr404(profileId);
        friendshipService.denyFriendship(profile, friendshipId);
    }

    @DeleteMapping("/{friendshipId}")
    public void deleteFriendship(@PathVariable String profileId, @PathVariable String friendshipId) {
        Profile profile = resolveProfileOr404(profileId);
        friendshipService.removeFriendshipByFriendship(profile, friendshipId);
    }
}
