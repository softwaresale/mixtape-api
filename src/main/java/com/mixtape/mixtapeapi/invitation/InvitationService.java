package com.mixtape.mixtapeapi.invitation;

import com.mixtape.mixtapeapi.playlist.Playlist;
import com.mixtape.mixtapeapi.playlist.PlaylistService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final PlaylistService playlistService;

    public InvitationService(InvitationRepository invitationRepository, PlaylistService playlistService) {
        this.invitationRepository = invitationRepository;
        this.playlistService = playlistService;
    }

    public Optional<Invitation> findInvitation(String id) {
        return invitationRepository.findById(id);
    }

    public Invitation save(Invitation newInvitation) {
        return invitationRepository.save(newInvitation);
    }

    public Optional<Playlist> createPlaylistFromInvitationId(String id) {
        // Grab invitation
        Optional<Invitation> invitation = findInvitation(id);

        // Check if exists
        if (invitation.isEmpty()) return Optional.empty();

        // Call playlist service to complete work
        return Optional.of(playlistService.createPlaylistFromInvitation(invitation.get()));
    }
}
