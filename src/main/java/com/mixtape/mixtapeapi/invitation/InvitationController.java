package com.mixtape.mixtapeapi.invitation;

import com.mixtape.mixtapeapi.friendship.Friendship;
import com.mixtape.mixtapeapi.playlist.Playlist;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invitation")
public class InvitationController {
    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @GetMapping("/{id}")
    public Invitation getById(@PathVariable String id) {
        return invitationService.findInvitation(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Invitation createNew(@RequestBody InvitationDTOs.Create newInvitation) {
        return invitationService.createNewInvitation(newInvitation);
    }

    @DeleteMapping("/{id}")
    public Invitation deleteById(@PathVariable String id) {
        return invitationService.deleteInvitation(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/{id}/playlist")
    public Playlist createNewPlaylist(@PathVariable String id) {
        return invitationService.createPlaylistFromInvitationId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/{id}/friendship")
    public Friendship createNewFriendship(@PathVariable String id) {
        return invitationService.createFriendshipFromInvitationId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
