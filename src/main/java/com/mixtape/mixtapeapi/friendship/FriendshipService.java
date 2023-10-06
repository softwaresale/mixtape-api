package com.mixtape.mixtapeapi.friendship;

import com.mixtape.mixtapeapi.invitation.Invitation;
import com.mixtape.mixtapeapi.invitation.InvitationRepository;
import com.mixtape.mixtapeapi.invitation.InvitationService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;

    public FriendshipService(FriendshipRepository friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    public Optional<Friendship> findFriendship(String id) {
        return friendshipRepository.findById(id);
    }

    public Friendship createFriendshipFromInvitation(Invitation invitation) {
        // Create Friendship
        Friendship newFriendship = new Friendship(null, invitation.getInitiator(), invitation.getTarget());
        // Save to repository
        return friendshipRepository.save(newFriendship);
    }
}
