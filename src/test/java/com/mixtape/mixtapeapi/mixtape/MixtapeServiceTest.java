package com.mixtape.mixtapeapi.mixtape;

import com.mixtape.mixtapeapi.playlist.Playlist;
import com.mixtape.mixtapeapi.playlist.PlaylistService;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.tracks.TrackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MixtapeServiceTest {
    @Mock
    MixtapeRepository mockMixtapeRepository;
    @Mock
    ReactionRepository mockReactionRepository;

    @Mock
    PlaylistService mockPlaylistService;

    @Mock
    TrackService mockTrackService;

    MixtapeService mixtapeService;

    @BeforeEach
    void beforeEach() {
        mixtapeService = new MixtapeService(mockMixtapeRepository, mockReactionRepository, mockPlaylistService, mockTrackService);
    }

    @Test
    void createMixtapeForPlaylist_shouldAddMixtapeToPlaylist_whenSuccessful() throws IOException {
        String mockPlaylistId = "playlist-id";
        Playlist mockPlaylist = new Playlist();
        mockPlaylist.setId(mockPlaylistId);
        Profile mockProfile = new Profile();
        Mixtape mockMixtape = new Mixtape();

        when(mockTrackService.getMixtapeDuration(mockMixtape)).thenReturn(Duration.ofMillis(4000));
        when(mockPlaylistService.findPlaylist(mockPlaylistId)).thenReturn(Optional.of(mockPlaylist));
        when(mockMixtapeRepository.save(mockMixtape)).thenReturn(mockMixtape);

        Mixtape newMixtape = mixtapeService.createMixtapeForPlaylist(mockProfile, mockPlaylistId, mockMixtape);
        assertThat(newMixtape).isEqualTo(mockMixtape);
        assertThat(newMixtape.getCreator()).isEqualTo(mockProfile);
        assertThat(newMixtape.getPlaylistID()).isEqualTo(mockPlaylistId);
        assertThat(newMixtape.getDurationMS()).isEqualTo(4000);
        assertThat(mockPlaylist.getMixtapes()).contains(mockMixtape);

        verify(mockPlaylistService).findPlaylist(mockPlaylistId);
        verify(mockTrackService).getMixtapeDuration(mockMixtape);
        verify(mockPlaylistService).save(mockPlaylist);
        verify(mockMixtapeRepository).save(mockMixtape);
    }

    @Test
    void createOrUpdateReactionForMixtape_doesNothing_whenReactionAlreadyExists() throws IOException {
        Mixtape mixtape = new Mixtape();
        mixtape.setId("mixtape-id");
        Profile reactingUser = new Profile();
        reactingUser.setId("user-id");

        when(mockMixtapeRepository.findById("mixtape-id")).thenReturn(Optional.of(mixtape));
        when(mockReactionRepository.existsByReactorAndMixtapeAndReactionType(reactingUser, mixtape, ReactionType.LIKE)).thenReturn(true);

        Mixtape existingMixtape = mixtapeService.createOrUpdateReactionForMixtape(mixtape.getId(), reactingUser, ReactionType.LIKE);

        // this should just be passed down
        assertThat(existingMixtape).isSameAs(mixtape);

        verify(mockReactionRepository).existsByReactorAndMixtapeAndReactionType(reactingUser, mixtape, ReactionType.LIKE);
        verify(mockReactionRepository, never()).findByReactorAndMixtape(any(), any());
        verify(mockReactionRepository, never()).save(any());
        verify(mockMixtapeRepository, never()).save(any());
    }

    @Test
    void createOrUpdateReactionForMixtape_createsNewReaction_forNewReactionFromUser() throws IOException {
        Mixtape mixtape = new Mixtape();
        mixtape.setId("mixtape-id");
        Profile reactingUser = new Profile();
        reactingUser.setId("user-id");

        when(mockMixtapeRepository.findById("mixtape-id")).thenReturn(Optional.of(mixtape));
        when(mockReactionRepository.existsByReactorAndMixtapeAndReactionType(reactingUser, mixtape, ReactionType.LIKE)).thenReturn(false);
        when(mockReactionRepository.findByReactorAndMixtape(reactingUser, mixtape)).thenReturn(Optional.empty());
        when(mockReactionRepository.save(any())).then((Answer<Reaction>) reaction -> (Reaction) reaction.getArguments()[0]);
        when(mockMixtapeRepository.save(any())).then((Answer<Mixtape>) reaction -> (Mixtape) reaction.getArguments()[0]);

        Mixtape existingMixtape = mixtapeService.createOrUpdateReactionForMixtape(mixtape.getId(), reactingUser, ReactionType.LIKE);

        // this should just be passed down
        assertThat(existingMixtape).isSameAs(mixtape);
        assertThat(existingMixtape.getReactions()).contains(new Reaction(null, reactingUser, ReactionType.LIKE, mixtape));

        verify(mockReactionRepository).existsByReactorAndMixtapeAndReactionType(reactingUser, mixtape, ReactionType.LIKE);
        verify(mockReactionRepository).findByReactorAndMixtape(reactingUser, mixtape);
        verify(mockReactionRepository).save(any());
        verify(mockMixtapeRepository).save(any());
    }

    @Test
    void createOrUpdateReactionForMixtape_updatesExistingReaction_forExistingReaction() throws IOException {
        Mixtape mixtape = new Mixtape();
        mixtape.setId("mixtape-id");
        Profile reactingUser = new Profile();
        reactingUser.setId("user-id");
        Reaction existingReaction = new Reaction(0, reactingUser, ReactionType.LIKE, mixtape);
        mixtape.addReaction(existingReaction);

        when(mockMixtapeRepository.findById("mixtape-id")).thenReturn(Optional.of(mixtape));
        when(mockReactionRepository.existsByReactorAndMixtapeAndReactionType(reactingUser, mixtape, ReactionType.DISLIKE)).thenReturn(false);
        when(mockReactionRepository.findByReactorAndMixtape(reactingUser, mixtape)).thenReturn(Optional.of(existingReaction));
        when(mockReactionRepository.save(any())).then((Answer<Reaction>) reaction -> (Reaction) reaction.getArguments()[0]);
        when(mockMixtapeRepository.save(any())).then((Answer<Mixtape>) reaction -> (Mixtape) reaction.getArguments()[0]);

        Mixtape existingMixtape = mixtapeService.createOrUpdateReactionForMixtape(mixtape.getId(), reactingUser, ReactionType.DISLIKE);

        // this should just be passed down
        assertThat(existingMixtape).isSameAs(mixtape);
        assertThat(existingMixtape.getReactions()).containsExactly(new Reaction(0, reactingUser, ReactionType.DISLIKE, mixtape));

        verify(mockReactionRepository).existsByReactorAndMixtapeAndReactionType(reactingUser, mixtape, ReactionType.DISLIKE);
        verify(mockReactionRepository).findByReactorAndMixtape(reactingUser, mixtape);
        verify(mockReactionRepository).save(any());
        verify(mockMixtapeRepository).save(any());
    }
}
