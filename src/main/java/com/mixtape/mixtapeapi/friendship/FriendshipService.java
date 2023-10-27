package com.mixtape.mixtapeapi.friendship;

import com.mixtape.mixtapeapi.invitation.Invitation;
import com.mixtape.mixtapeapi.profile.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<Profile> findFriendsForProfile(Profile profile) {
        return friendshipRepository.findAllByInitiatorOrTarget(profile, profile).stream()
                .map(friendship -> {
                    if (friendship.getTarget().getId().equals(profile.getId())) {
                        return friendship.getInitiator();
                    } else {
                        return friendship.getTarget();
                    }
                })
                .collect(Collectors.toList());
    }
}
