package com.mixtape.mixtapeapi.friendship;

import com.mixtape.mixtapeapi.invitation.Invitation;
import com.mixtape.mixtapeapi.invitation.InvitationRepository;
import org.springframework.stereotype.Service;

@Service
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final InvitationRepository invitationRepository;

    public FriendshipService(FriendshipRepository friendshipRepository, InvitationRepository invitationRepository) {
        this.friendshipRepository = friendshipRepository;
        this.invitationRepository = invitationRepository;
    }

    public Friendship findFriendship(String id) {
        return friendshipRepository.getReferenceById(id);
    }

    public Friendship createFriendship(Invitation invitation) {
        // Create Friendship
        Friendship newFriendship = new Friendship(null, invitation.getInitiatorID(), invitation.getTargetID());

        // Delete Invitation
        invitationRepository.delete(invitation);

        // Save to repository
        return friendshipRepository.save(newFriendship);
    }
}
