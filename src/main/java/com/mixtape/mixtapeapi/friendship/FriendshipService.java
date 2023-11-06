package com.mixtape.mixtapeapi.friendship;

import com.mixtape.mixtapeapi.notification.NotificationService;
import com.mixtape.mixtapeapi.notification.NotificationType;
import com.mixtape.mixtapeapi.playlist.PlaylistService;
import com.mixtape.mixtapeapi.profile.Profile;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final PlaylistService playlistService;
    private final NotificationService notificationService;

    public FriendshipService(FriendshipRepository friendshipRepository, PlaylistService playlistService, NotificationService notificationService) {
        this.friendshipRepository = friendshipRepository;
        this.playlistService = playlistService;
        this.notificationService = notificationService;
    }

    public Optional<Friendship> findFriendship(String friendshipId) {
        return friendshipRepository.findById(friendshipId);
    }

    public List<Profile> findFriendsForProfile(Profile profile) {
        return friendshipRepository.findAllByInitiatorAndTargetNotNullOrTarget(profile, profile).stream()
                .map(friendship -> {
                    if (friendship.getTarget().getId().equals(profile.getId())) {
                        return friendship.getInitiator();
                    } else {
                        return friendship.getTarget();
                    }
                })
                .toList();
    }

    public Friendship createFriendship(Profile initiator, Profile requestedTarget) {
        // Check if full friendship already exists
        if (friendshipRepository.existsByInitiatorAndTarget(initiator, requestedTarget) ||
                friendshipRepository.existsByInitiatorAndTarget(requestedTarget, initiator)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friendship between these two already exists");
        }

        // Check if partial friendship already exists
        if (notificationService.notificationExistsByBothProfiles(initiator, requestedTarget)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friendship request has been sent out already for these two");
        }

        // Create partial friendship
        Friendship friendship = new Friendship(null, initiator, null);
        friendship = friendshipRepository.save(friendship);

        // Create contents and notification for accepting or denying playlist
        String contents = String.format("%s wants to be friends with you", initiator.getDisplayName());
        notificationService.createNotificationFromTrigger(friendship.getId(), initiator, requestedTarget, contents, NotificationType.FRIENDSHIP);

        return friendship;
    }

    @Transactional
    public Friendship acceptFriendship(Profile target, String friendshipId) {
        // Grab friendship and delete corresponding notification
        Friendship friendship = grabFriendshipAndDeleteNotification(target, friendshipId);

        // Fill out fields to update
        friendship.setTarget(target);

        // Update friendship
        return friendshipRepository.save(friendship);
    }

    @Transactional
    public void denyFriendship(Profile target, String friendshipId) {
        // Grab friendship and delete corresponding notification
        Friendship friendship = grabFriendshipAndDeleteNotification(target, friendshipId);

        // Delete friendship
        friendshipRepository.delete(friendship);
    }

    private Friendship grabFriendshipAndDeleteNotification(Profile target, String friendshipId) {
        // Delete notification
        notificationService.deleteNotificationByTargetAndExternalId(target, friendshipId);

        // Return friendship
        return findFriendship(friendshipId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friendship does not exist"));
    }

    @Transactional
    public void removeFriendshipByFriendship(Profile profile, String friendshipId) {
        // Verify profile and friendship matches
        Friendship friendship = friendshipRepository
                .findByIdAndInitiatorOrTarget(friendshipId, profile, profile)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile with given friendship does not exist"));

        // Delete friendship and all dependent objects
        cascadeDeleteFriendship(friendship);
    }

    @Transactional
    public void removeFriendshipByFriend(Profile deleter, Profile deletee) {
        // Verify friendship exists
        Friendship friendship = friendshipRepository
                .findByTargetAndInitiatorOrInitiatorAndTarget(deleter, deletee, deleter, deletee)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No friendship between these profiles exist"));

        // Delete friendship and all dependent objects
        cascadeDeleteFriendship(friendship);
    }

    private void cascadeDeleteFriendship(Friendship friendship) {
        // Delete all playlists between them
        playlistService.removePlaylistsByInitiatorAndTarget(friendship.getInitiator(), friendship.getTarget());

        // Delete all notifications
        notificationService.deleteNotificationsByBothProfiles(friendship.getInitiator(), friendship.getTarget());

        // Delete friendship
        friendshipRepository.delete(friendship);
    }
}
