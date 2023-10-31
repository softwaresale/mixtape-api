package com.mixtape.mixtapeapi.friendship;

import com.mixtape.mixtapeapi.notification.NotificationService;
import com.mixtape.mixtapeapi.playlist.Playlist;
import com.mixtape.mixtapeapi.playlist.PlaylistService;
import com.mixtape.mixtapeapi.profile.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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

    public Friendship createFriendship(Profile initiator, Friendship newFriendship, Profile requestedTarget) {
        // Verify initiator is same with friendship
        if (!initiator.equals(newFriendship.getInitiator())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Initiator does not match current user");
        }

        // Create Friendship
        Friendship friendship = new Friendship(null, initiator, null);
        friendshipRepository.save(friendship);

        // Create notification for accepting or denying playlist
        notificationService.createNotificationFromFriendship(friendship, requestedTarget);

        return friendship;
    }

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

    public void denyFriendship(Profile target, String friendshipId) {
        // Grab friendship
        Friendship friendship = findFriendship(friendshipId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friendship does not exist"));

        // Delete notification
        notificationService.deleteNotificationFromFriendship(friendship, target);

        // Delete friendship
        friendshipRepository.delete(friendship);
    }

    public void removeFriendship(String friendshipId) throws IOException {
        // Grab friendship
        Friendship friendship = findFriendship(friendshipId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Friendship does not exist"));

        // Delete playlists between them
        playlistService.findPlaylistsForProfile(friendship.getInitiator())
                .stream()
                .filter(playlist -> playlist.getTarget().equals(friendship.getTarget()))
                .map(Playlist::getId)
                .forEach(playlistService::removePlaylist);

        // Delete friendship
        friendshipRepository.delete(friendship);
    }
}
