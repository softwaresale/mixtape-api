package com.mixtape.mixtapeapi.playlist;

import com.mixtape.mixtapeapi.invitation.Invitation;
import com.mixtape.mixtapeapi.invitation.InvitationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final InvitationRepository invitationRepository;

    public PlaylistService(PlaylistRepository playlistRepository, InvitationRepository invitationRepository) {
        this.playlistRepository = playlistRepository;
        this.invitationRepository = invitationRepository;
    }

    public Optional<Playlist> findPlaylist(String id) {
        return playlistRepository.findById(id);
    }

    public Playlist save(Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    public Playlist createPlaylistFromInvitation(Invitation invitation) {
        // Create Playlist
        Playlist newPlaylist = new Playlist(null, null, null, invitation.getInitiatorID(), invitation.getTargetID(), null, null);

        // Delete Invitation
        invitationRepository.delete(invitation);

        // Save to repository
        return playlistRepository.save(newPlaylist);
    }

    public Optional<Playlist> updatePlaylist(Playlist playlist, String id) {
        // Create Optional
        Optional<Playlist> optionalPlaylist = Optional.empty();

        // If exists, add to optional
        if (playlistRepository.existsById(id)) {
            optionalPlaylist = Optional.of(playlistRepository.save(playlist));
        }

        // Return final optional
        return optionalPlaylist;
    }

}
