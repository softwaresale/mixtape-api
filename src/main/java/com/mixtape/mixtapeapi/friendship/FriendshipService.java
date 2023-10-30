package com.mixtape.mixtapeapi.friendship;

import com.mixtape.mixtapeapi.invitation.Invitation;
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

    public FriendshipService(FriendshipRepository friendshipRepository, PlaylistService playlistService) {
        this.friendshipRepository = friendshipRepository;
        this.playlistService = playlistService;
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

    public void deleteFriendship(String friendshipId) throws IOException {
        // Grab friendship
        Friendship friendship = findFriendship(friendshipId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friendship does not exist"));

        // Delete playlists between them
        playlistService.findPlaylistsForProfile(friendship.getInitiator())
                .stream()
                .filter(playlist -> playlist.getTarget().equals(friendship.getTarget()))
                .map(Playlist::getId)
                .forEach(playlistService::deleteById);
    }
}
