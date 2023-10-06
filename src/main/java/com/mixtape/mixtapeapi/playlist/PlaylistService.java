package com.mixtape.mixtapeapi.playlist;

import com.mixtape.mixtapeapi.invitation.Invitation;
import com.mixtape.mixtapeapi.invitation.InvitationRepository;
import com.mixtape.mixtapeapi.invitation.InvitationService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;

    public PlaylistService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public Optional<Playlist> findPlaylist(String id) {
        return playlistRepository.findById(id);
    }

    public Playlist save(Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    public Playlist createPlaylistFromInvitation(Invitation invitation) {
        // Create Playlist
        String defaultName = String.format("%s and %s's playlist", invitation.getInitiator().getDisplayName(), invitation.getTarget().getDisplayName());
        Playlist newPlaylist = new Playlist(null, null, defaultName, invitation.getInitiator(), invitation.getTarget(), null, null);

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
