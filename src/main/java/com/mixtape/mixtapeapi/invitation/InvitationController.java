package com.mixtape.mixtapeapi.invitation;

import com.mixtape.mixtapeapi.friendship.Friendship;
import com.mixtape.mixtapeapi.friendship.FriendshipService;
import com.mixtape.mixtapeapi.playlist.Playlist;
import com.mixtape.mixtapeapi.playlist.PlaylistService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/invitation")
public class InvitationController {
    private final InvitationService invitationService;
    private final PlaylistService playlistService;
    private final FriendshipService friendshipService;

    public InvitationController(InvitationService invitationService, PlaylistService playlistService, FriendshipService friendshipService) {
        this.invitationService = invitationService;
        this.playlistService = playlistService;
        this.friendshipService = friendshipService;
    }

    @GetMapping("/{id}")
    public Invitation getById(@PathVariable String id) {
        return invitationService.findInvitation(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Invitation createNew(Invitation newInvitation) {
        return invitationService.save(newInvitation);
    }

    @PostMapping("/playlist")
    public Playlist createNewPlaylist(@RequestBody Invitation invitation) {
        return playlistService.createPlaylistFromInvitation(invitation);
    }

    @PostMapping("/friendship")
    public Friendship createNewFriendship(@RequestBody Invitation invitation) {
        return friendshipService.createFriendshipFromInvitation(invitation);
    }
}
