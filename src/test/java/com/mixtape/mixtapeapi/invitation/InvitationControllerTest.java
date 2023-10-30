package com.mixtape.mixtapeapi.invitation;

import com.mixtape.mixtapeapi.friendship.Friendship;
import com.mixtape.mixtapeapi.playlist.Playlist;
import com.mixtape.mixtapeapi.playlist.PlaylistService;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvitationControllerTest {

    static Profile mockInitiator = new Profile("prof-1", "", "user1", "");
    static Profile mockTarget = new Profile("prof-2", "", "user2", "");
    static Invitation mockInvitation = new Invitation("inv1", mockInitiator, mockTarget, null, null);

    @Mock
    ProfileService mockProfileService;
    @Mock
    InvitationService mockInvitationService;
    @Mock
    PlaylistService mockPlaylistService;

    InvitationController controller;

    @BeforeEach
    void setUp() {
        controller = new InvitationController(mockProfileService, mockInvitationService, mockPlaylistService);
    }

    @Test
    void acceptInvitation_shouldCreateFriendship_whenCalledWithFriendship() {
        mockInvitation.setInvitationType(InvitationType.FRIENDSHIP);
        mockInvitation.setInvitedObjectID(mockTarget.getId());
        when(mockProfileService.findProfile(mockTarget.getId())).thenReturn(Optional.of(mockTarget));
        when(mockInvitationService.findInvitation(mockInvitation.getId())).thenReturn(Optional.of(mockInvitation));
        when(mockInvitationService.createFriendshipFromInvitation(mockInvitation)).thenReturn(new Friendship("id", mockInitiator, mockTarget));

        Object result = controller.acceptInvitation(mockTarget.getId(), mockInvitation.getId());
        assertThat(result).isInstanceOf(Friendship.class);
        Friendship friendship = (Friendship) result;
        assertThat(friendship.getTarget()).isEqualTo(mockTarget);
        assertThat(friendship.getInitiator()).isEqualTo(mockInitiator);

        verify(mockProfileService).findProfile(mockTarget.getId());
        verify(mockInvitationService).findInvitation(mockInvitation.getId());
        verify(mockInvitationService).createFriendshipFromInvitation(mockInvitation);
        verify(mockPlaylistService, never()).acceptPlaylistInvitation(any(), any());
        verify(mockInvitationService).delete(mockInvitation);
    }

    @Test
    void acceptInvitation_shouldCreatePlaylist_whenPlaylistMode() {
        mockInvitation.setInvitationType(InvitationType.PLAYLIST);
        mockInvitation.setInvitedObjectID("playlist-id");
        when(mockProfileService.findProfile(mockTarget.getId())).thenReturn(Optional.of(mockTarget));
        when(mockInvitationService.findInvitation(mockInvitation.getId())).thenReturn(Optional.of(mockInvitation));
        Playlist mockPlaylist = new Playlist("playlist-id", "name", "", mockInitiator, mockTarget, "", "");
        when(mockPlaylistService.acceptPlaylistInvitation(mockTarget, "playlist-id")).thenReturn(mockPlaylist);

        Object result = controller.acceptInvitation(mockTarget.getId(), mockInvitation.getId());
        assertThat(result).isInstanceOf(Playlist.class);
        Playlist playlist = (Playlist) result;
        assertThat(playlist).isEqualTo(mockPlaylist);

        verify(mockProfileService).findProfile(mockTarget.getId());
        verify(mockInvitationService).findInvitation(mockInvitation.getId());
        verify(mockInvitationService, never()).createFriendshipFromInvitation(mockInvitation);
        verify(mockPlaylistService).acceptPlaylistInvitation(mockTarget, "playlist-id");
        verify(mockInvitationService).delete(mockInvitation);
    }

    @Test
    void acceptInvitation_shouldThrowBadRequest_whenInitiatorTriesToAccept() {
        mockInvitation.setInvitationType(InvitationType.FRIENDSHIP);
        mockInvitation.setInvitedObjectID(mockTarget.getId());
        when(mockProfileService.findProfile(mockInitiator.getId())).thenReturn(Optional.of(mockInitiator));
        when(mockInvitationService.findInvitation(mockInvitation.getId())).thenReturn(Optional.of(mockInvitation));

        assertThatThrownBy(() -> {
            controller.acceptInvitation(mockInitiator.getId(), mockInvitation.getId());
        }).isInstanceOf(ResponseStatusException.class);

        verify(mockProfileService).findProfile(mockInitiator.getId());
        verify(mockInvitationService).findInvitation(mockInvitation.getId());
        verify(mockInvitationService, never()).createFriendshipFromInvitation(mockInvitation);
        verify(mockPlaylistService, never()).acceptPlaylistInvitation(any(), any());
        verify(mockInvitationService, never()).delete(mockInvitation);
    }

    @Test
    void declineInvitation_deletesInvitation_whenFriendship() {
        mockInvitation.setInvitationType(InvitationType.FRIENDSHIP);
        mockInvitation.setInvitedObjectID(mockTarget.getId());
        when(mockProfileService.findProfile(mockTarget.getId())).thenReturn(Optional.of(mockTarget));
        when(mockInvitationService.findInvitation(mockInvitation.getId())).thenReturn(Optional.of(mockInvitation));

        controller.declineInvitation(mockTarget.getId(), mockInvitation.getId());

        verify(mockProfileService).findProfile(mockTarget.getId());
        verify(mockInvitationService).findInvitation(mockInvitation.getId());
        verify(mockPlaylistService, never()).removePlaylist(any());
        verify(mockInvitationService).delete(mockInvitation);
    }

    @Test
    void declineInvitation_deletesInvitationAndPlaylist_whenPlaylist() {
        mockInvitation.setInvitationType(InvitationType.PLAYLIST);
        mockInvitation.setInvitedObjectID("playlist-id");
        when(mockProfileService.findProfile(mockTarget.getId())).thenReturn(Optional.of(mockTarget));
        when(mockInvitationService.findInvitation(mockInvitation.getId())).thenReturn(Optional.of(mockInvitation));

        controller.declineInvitation(mockTarget.getId(), mockInvitation.getId());

        verify(mockProfileService).findProfile(mockTarget.getId());
        verify(mockInvitationService).findInvitation(mockInvitation.getId());
        verify(mockPlaylistService).removePlaylist("playlist-id");
        verify(mockInvitationService).delete(mockInvitation);
    }

    @Test
    void declineInvitation_throwsBadRequest_whenInitiatorDoesTheThing() {
        mockInvitation.setInvitationType(InvitationType.FRIENDSHIP);
        mockInvitation.setInvitedObjectID(mockTarget.getId());
        when(mockProfileService.findProfile(mockInitiator.getId())).thenReturn(Optional.of(mockInitiator));
        when(mockInvitationService.findInvitation(mockInvitation.getId())).thenReturn(Optional.of(mockInvitation));

        assertThatThrownBy(() -> {
            controller.declineInvitation(mockInitiator.getId(), mockInvitation.getId());
        }).isInstanceOf(ResponseStatusException.class);

        verify(mockProfileService).findProfile(mockInitiator.getId());
        verify(mockInvitationService).findInvitation(mockInvitation.getId());
        verify(mockPlaylistService, never()).removePlaylist(any());
        verify(mockInvitationService, never()).delete(mockInvitation);
    }
}
