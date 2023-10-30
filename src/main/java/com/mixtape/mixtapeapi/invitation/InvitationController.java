package com.mixtape.mixtapeapi.invitation;

import com.mixtape.mixtapeapi.AbstractRestController;
import com.mixtape.mixtapeapi.playlist.PlaylistService;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile/{profileId}/invitation")
public class InvitationController extends AbstractRestController {
    private final InvitationService invitationService;
    private final PlaylistService playlistService;

    public InvitationController(ProfileService profileService, InvitationService invitationService, PlaylistService playlistService) {
        super(profileService);
        this.invitationService = invitationService;
        this.playlistService = playlistService;
    }

    @GetMapping("/incoming")
    public List<Invitation> getIncomingInvitations(@PathVariable String profileId) {
        Profile profile = resolveProfileOr404(profileId);
        return invitationService.getIncomingInvitations(profile);
    }

    @GetMapping("/outgoing")
    public List<Invitation> getOutgoingInvitations(@PathVariable String profileId) {
        Profile profile = resolveProfileOr404(profileId);
        return invitationService.getOutgoingInvitations(profile);
    }

    @GetMapping("/{id}")
    public Invitation getById(@PathVariable String profileId, @PathVariable String id) {
        return invitationService.findInvitation(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Invitation createNew(@PathVariable String profileId, @RequestBody InvitationDTOs.Create newInvitation) {
        Profile initiator = resolveProfileOr404(profileId);
        return invitationService.createNewInvitation(initiator, newInvitation);
    }

    @DeleteMapping("/{id}")
    public Invitation deleteById(@PathVariable String id) {
        return invitationService.deleteInvitation(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/{id}/accept")
    public Object acceptInvitation(@PathVariable String profileId, @PathVariable String id) {
        Profile acceptor = resolveProfileOr404(profileId);
        Invitation invitation = invitationService.findInvitation(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!acceptor.equals(invitation.getTarget())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The provided user cannot modify this invitation");
        }

        Object result = switch (invitation.getInvitationType()) {
            case FRIENDSHIP -> invitationService.createFriendshipFromInvitation(invitation);
            case PLAYLIST -> playlistService.acceptPlaylistInvitation(acceptor, invitation.getInvitedObjectID());
        };

        invitationService.delete(invitation);

        return result;
    }

    @PostMapping("/{id}/decline")
    public void declineInvitation(@PathVariable String profileId, @PathVariable String id) {
        Profile acceptor = resolveProfileOr404(profileId);
        Invitation invitation = invitationService.findInvitation(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!acceptor.equals(invitation.getTarget())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The provided user cannot modify this invitation");
        }

        switch (invitation.getInvitationType()) {
            case FRIENDSHIP -> {
            }
            case PLAYLIST -> {
                playlistService.removePlaylist(invitation.getInvitedObjectID());
            }
        }

        invitationService.delete(invitation);
    }
}
