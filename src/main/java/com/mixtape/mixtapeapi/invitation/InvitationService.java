package com.mixtape.mixtapeapi.invitation;

import com.mixtape.mixtapeapi.friendship.Friendship;
import com.mixtape.mixtapeapi.friendship.FriendshipService;
import com.mixtape.mixtapeapi.playlist.Playlist;
import com.mixtape.mixtapeapi.playlist.PlaylistService;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final PlaylistService playlistService;
    private final FriendshipService friendshipService;
    private final ProfileService profileService;


    public InvitationService(InvitationRepository invitationRepository, PlaylistService playlistService, FriendshipService friendshipService, ProfileService profileService) {
        this.invitationRepository = invitationRepository;
        this.playlistService = playlistService;
        this.friendshipService = friendshipService;
        this.profileService = profileService;
    }

    public Optional<Invitation> findInvitation(String id) {
        return invitationRepository.findById(id);
    }

    public Invitation createNewInvitation(InvitationDTOs.Create newInvitation) {
        Profile initiator = profileService.findProfile(newInvitation.getInitiatorId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Initiator not found"));
        Profile target = profileService.findProfile(newInvitation.getTargetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Target not found"));

        Invitation entity = new Invitation(null, initiator, target, newInvitation.getInvitationType());
        return invitationRepository.save(entity);
    }

    public Invitation save(Invitation newInvitation) {
        return invitationRepository.save(newInvitation);
    }

    public Optional<Invitation> deleteInvitation(String id) {
        // Try to find invitation
        Optional<Invitation> optionalInvitation = findInvitation(id);

        // Delete if exists
        invitationRepository.deleteById(id);

        // Return optional (empty if does not exist)
        return optionalInvitation;
    }

    public Optional<Playlist> createPlaylistFromInvitationId(String id) {
        // Grab invitation
        Optional<Invitation> invitation = findInvitation(id);

        // Check if exists
        if (invitation.isEmpty()) return Optional.empty();

        // Call playlist service to complete work
        Playlist newPlaylist = playlistService.createPlaylistFromInvitation(invitation.get());
        delete(invitation.get());
        return Optional.of(newPlaylist);
    }

    public Optional<Friendship> createFriendshipFromInvitationId(String id) {
        // Grab invitation
        Optional<Invitation> invitation = findInvitation(id);

        // Check if exists
        if (invitation.isEmpty()) return Optional.empty();

        // Call playlist service to complete work
        Friendship newFriendship = friendshipService.createFriendshipFromInvitation(invitation.get());
        delete(invitation.get());

        return Optional.of(newFriendship);
    }

    public void delete(Invitation invitation) {
        this.invitationRepository.delete(invitation);
    }
}
