package com.mixtape.mixtapeapi.playlist;

import com.mixtape.mixtapeapi.invitation.Invitation;
import com.mixtape.mixtapeapi.invitation.InvitationRepository;
import org.springframework.stereotype.Service;

@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final InvitationRepository invitationRepository;

    public PlaylistService(PlaylistRepository playlistRepository, InvitationRepository invitationRepository) {
        this.playlistRepository = playlistRepository;
        this.invitationRepository = invitationRepository;
    }

    public Playlist findPlaylist(String id) {
        return playlistRepository.getReferenceById(id);
    }

    public Playlist createPlaylist(Invitation invitation) {
        // Create Playlist
        Playlist newPlaylist = new Playlist(null, null, null, invitation.getInitiatorID(), invitation.getTargetID(), null, null);

        // Delete Invitation
        invitationRepository.delete(invitation);

        // Save to repository
        return playlistRepository.save(newPlaylist);
    }

    public Playlist updatePlaylist(Playlist playlist) {
        return playlistRepository.save(playlist);
    }

}
