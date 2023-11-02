package com.mixtape.mixtapeapi.friendship;

import com.mixtape.mixtapeapi.notification.NotificationService;
import com.mixtape.mixtapeapi.playlist.PlaylistService;
import com.mixtape.mixtapeapi.profile.Profile;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .collect(Collectors.toList());
    }

    public Friendship createFriendship(Profile initiator, Profile requestedTarget) {
        // Create friendship
        Friendship friendship = new Friendship(null, initiator, null);
        friendship = friendshipRepository.save(friendship);

        // Create notification for accepting or denying playlist
        notificationService.createNotificationFromFriendship(friendship, requestedTarget);

        return friendship;
    }

    @Transactional
    public Friendship acceptFriendship(Profile target, String friendshipId) {
        // Grab friendship
        Friendship friendship = findFriendship(friendshipId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friendship does not exist"));

        // Delete notification
        notificationService.deleteNotificationFromFriendship(friendship, target);

        // Fill out fields to update
        friendship.setTarget(target);

        // Update friendship
        return friendshipRepository.save(friendship);
    }

    @Transactional
    public void denyFriendship(Profile target, String friendshipId) {
        // Grab friendship
        Friendship friendship = findFriendship(friendshipId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friendship does not exist"));

        // Delete notification
        notificationService.deleteNotificationFromFriendship(friendship, target);

        // Delete friendship
        friendshipRepository.delete(friendship);
    }

    public void removeFriendship(Profile profile, String friendshipId) {
        // Verify profile and friendship matches
        Friendship friendship = friendshipRepository.findByIdAndInitiatorOrTarget(friendshipId, profile, profile)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile with given friendship does not exist"));

        cascadeDeleteFriendship(friendship);
    }

    public void removeFriendshipWithFriend(Profile deleter, Profile deletee) {
        Friendship friendship = friendshipRepository
                .findByTargetAndInitiatorOrInitiatorAndTarget(deleter, deletee, deleter, deletee)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No friendship between these profiles exist"));
        this.cascadeDeleteFriendship(friendship);
    }

    private void cascadeDeleteFriendship(Friendship friendship) {
        // Delete all playlists between them
        playlistService.removePlaylistsByInitiatorAndTarget(friendship.getInitiator(), friendship.getTarget());

        // Delete friendship
        friendshipRepository.delete(friendship);
    }
}
