package com.mixtape.mixtapeapi.invitation;

import com.mixtape.mixtapeapi.friendship.Friendship;
import com.mixtape.mixtapeapi.friendship.FriendshipService;
import com.mixtape.mixtapeapi.playlist.Playlist;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final FriendshipService friendshipService;
    private final ProfileService profileService;


    public InvitationService(InvitationRepository invitationRepository, FriendshipService friendshipService, ProfileService profileService) {
        this.invitationRepository = invitationRepository;
        this.friendshipService = friendshipService;
        this.profileService = profileService;
    }

    public Optional<Invitation> findInvitation(String id) {
        return invitationRepository.findById(id);
    }

    public Invitation createNewInvitation(Profile initiator, InvitationDTOs.Create newInvitation) {
        Profile target = profileService.findProfile(newInvitation.getTargetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Target not found"));

        Invitation entity = new Invitation(null, initiator, target, newInvitation.getInvitationType(), initiator.getId());
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

    public void createInvitationFromPlaylist(Playlist playlist, Profile requestedProfile) {
        Invitation invitation = new Invitation(null, playlist.getInitiator(), requestedProfile, InvitationType.PLAYLIST, playlist.getId());
        save(invitation);
    }

    public Friendship createFriendshipFromInvitation(Invitation invitation) {
        // Call playlist service to complete work
        Friendship newFriendship = friendshipService.createFriendshipFromInvitation(invitation);
        delete(invitation);

        return newFriendship;
    }

    public void delete(Invitation invitation) {
        this.invitationRepository.delete(invitation);
    }
}
