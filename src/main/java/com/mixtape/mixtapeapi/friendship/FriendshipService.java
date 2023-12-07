package com.mixtape.mixtapeapi.friendship;

import com.mixtape.mixtapeapi.notification.NotificationService;
import com.mixtape.mixtapeapi.notification.NotificationType;
import com.mixtape.mixtapeapi.playlist.Playlist;
import com.mixtape.mixtapeapi.playlist.PlaylistService;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.blocking.BlockedActionService;
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
    private final BlockedActionService blockedActionService;

    public FriendshipService(FriendshipRepository friendshipRepository,
                             PlaylistService playlistService,
                             NotificationService notificationService,
                             BlockedActionService blockedActionService) {
        this.friendshipRepository = friendshipRepository;
        this.playlistService = playlistService;
        this.notificationService = notificationService;
        this.blockedActionService = blockedActionService;
    }

    public Optional<Friendship> findFriendship(String friendshipId) {
        return friendshipRepository.findById(friendshipId);
    }

    public Optional<Friendship> findFriendshipBetweenProfiles(Profile firstProfile, Profile secondProfile) {
        return friendshipRepository.findByTargetAndInitiatorOrInitiatorAndTarget(firstProfile, secondProfile, firstProfile, secondProfile);
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

    public FriendshipInfo findFriendshipInfoForFriend(Profile profile, Profile friend) {
        // Grab shared playlists
        List<Playlist> sharedPlaylists = playlistService.findPlaylistsByInitiatorOrTarget(profile, friend);

        // Find number of mixtapes made by profile
        int numMixtapesFromProfile = findNumMixtapesByCreatorFromPlaylists(profile, sharedPlaylists);

        // Find number of mixtapes made by friend
        int numMixtapesFromFriend = findNumMixtapesByCreatorFromPlaylists(friend, sharedPlaylists);

        // Return newly created info
        return new FriendshipInfo(sharedPlaylists, numMixtapesFromProfile, numMixtapesFromFriend);
    }

    private int findNumMixtapesByCreatorFromPlaylists(Profile creator, List<Playlist> playlists) {
        return (int) playlists
                .stream()
                .map(Playlist::getMixtapes)
                .flatMap(List::stream)
                .filter(mixtape -> mixtape.getCreator().equals(creator))
                .count();
    }

    public Friendship createFriendship(Profile initiator, Profile requestedTarget) {
        // Check if full friendship already exists
        if (friendshipRepository.existsByInitiatorAndTarget(initiator, requestedTarget) ||
                friendshipRepository.existsByInitiatorAndTarget(requestedTarget, initiator)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Friendship between these two already exists");
        }

        if (blockedActionService.isBlockedSymmetrical(initiator, requestedTarget)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Profiles are blocked");
        }

        // Check if partial friendship already exists that initiator has created
        if (notificationService.friendshipNotificationExistsByInitiatorAndTarget(initiator, requestedTarget)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already sent out a friendship request for this user");
        }

        // Check if partial friendship already exists that target has created
        if (notificationService.friendshipNotificationExistsByInitiatorAndTarget(requestedTarget, initiator)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user has already sent you a friendship request");
        }

        // Create partial friendship
        Friendship friendship = new Friendship(null, initiator, null);
        friendship = friendshipRepository.save(friendship);

        // Create contents and notification for accepting or denying playlist
        String contents = String.format("%s wants to be friends with you", initiator.getDisplayName());
        notificationService.createNotificationFromTrigger(friendship.getId(), initiator, requestedTarget, contents, NotificationType.FRIENDSHIP, "");

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
        playlistService.removePlaylistsByBothProfiles(friendship.getInitiator(), friendship.getTarget());

        // Delete all pending playlists between
        playlistService
                .findPendingPlaylistsByInitiatorOrTarget(friendship.getInitiator(), friendship.getTarget())
                .stream()
                .map(Playlist::getId)
                .filter(playlistId -> notificationService.findPlaylistNotification(playlistId).isPresent())
                .forEach(playlistService::removePlaylistById);

        // Delete all notifications
        notificationService.deleteNotificationsByBothProfiles(friendship.getInitiator(), friendship.getTarget());

        // Delete friendship
        friendshipRepository.delete(friendship);
    }
}
