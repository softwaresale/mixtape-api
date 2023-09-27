package com.mixtape.mixtapeapi.friendship;

import com.mixtape.mixtapeapi.invitation.Invitation;
import com.mixtape.mixtapeapi.invitation.InvitationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final InvitationRepository invitationRepository;

    public FriendshipService(FriendshipRepository friendshipRepository, InvitationRepository invitationRepository) {
        this.friendshipRepository = friendshipRepository;
        this.invitationRepository = invitationRepository;
    }

    public Optional<Friendship> findFriendship(String id) {
        return friendshipRepository.findById(id);
    }

    public Friendship createFriendshipFromInvitation(Invitation invitation) {
        // Create Friendship
        Friendship newFriendship = new Friendship(null, invitation.getInitiatorID(), invitation.getTargetID());

        // Delete Invitation
        invitationRepository.delete(invitation);

        // Save to repository
        return friendshipRepository.save(newFriendship);
    }
}
