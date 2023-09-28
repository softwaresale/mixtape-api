package com.mixtape.mixtapeapi.playlist;

import com.mixtape.mixtapeapi.invitation.Invitation;
import com.mixtape.mixtapeapi.invitation.InvitationRepository;
import com.mixtape.mixtapeapi.mixtape.Mixtape;
import com.mixtape.mixtapeapi.mixtape.MixtapeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final InvitationRepository invitationRepository;
    private final MixtapeService mixtapeService;

    public PlaylistService(PlaylistRepository playlistRepository, InvitationRepository invitationRepository, MixtapeService mixtapeService) {
        this.playlistRepository = playlistRepository;
        this.invitationRepository = invitationRepository;
        this.mixtapeService = mixtapeService;
    }

    public Optional<Playlist> findPlaylist(String id) {
        return playlistRepository.findById(id);
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

    public Optional<List<Mixtape>> findMixtapesOfPlaylist(String id) {
        // Grab optional playlist
        Optional<Playlist> optionalPlaylist = findPlaylist(id);

        // Return empty if playlist does not exist
        if (optionalPlaylist.isEmpty()) return Optional.empty();

        // Return mixtapes of playlist
        return Optional.of(mixtapeService.findAllMixtapesByPlaylistId(optionalPlaylist.get().getId()));
    }

}
