package com.mixtape.mixtapeapi.mixtape;

import com.mixtape.mixtapeapi.notification.NotificationService;
import com.mixtape.mixtapeapi.notification.NotificationType;
import com.mixtape.mixtapeapi.playlist.Playlist;
import com.mixtape.mixtapeapi.playlist.PlaylistService;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.spotify.SpotifyService;
import com.mixtape.mixtapeapi.tracks.TrackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    @Mock
    NotificationService mockNotificationService;
    @Mock
    SpotifyService mockSpotifyService;

    MixtapeService mixtapeService;

    @BeforeEach
    void beforeEach() {
        mixtapeService = new MixtapeService(mockMixtapeRepository, mockReactionRepository, mockPlaylistService, mockTrackService, mockNotificationService, mockSpotifyService);
    }

    @Test
    void createMixtapeForPlaylist_shouldAddMixtapeToPlaylist_whenSuccessful() {
        String mockPlaylistId = "playlist-id";
        Profile mockProfile = new Profile();
        mockProfile.setId("id1");
        mockProfile.setDisplayName("name1");
        Profile mockTarget = new Profile();
        mockTarget.setId("id2");
        mockTarget.setDisplayName("name2");
        Mixtape mockMixtape = new Mixtape();
        mockMixtape.setName("mixtape1");
        mockMixtape.setId("mixtapeId1");
        Playlist mockPlaylist = new Playlist();
        mockPlaylist.setId(mockPlaylistId);
        mockPlaylist.setName("playlist1");
        mockPlaylist.setInitiator(mockProfile);
        mockPlaylist.setTarget(mockTarget);

        when(mockTrackService.getMixtapeDuration(mockMixtape)).thenReturn(Duration.ofMillis(4000));
        when(mockTrackService.inflateMixtape(mockMixtape)).thenReturn(mockMixtape);
        when(mockPlaylistService.findPlaylist(mockPlaylistId)).thenReturn(Optional.of(mockPlaylist));
        when(mockMixtapeRepository.save(mockMixtape)).thenReturn(mockMixtape);

        Mixtape newMixtape = mixtapeService.createMixtapeForPlaylist(mockProfile, mockPlaylistId, mockMixtape);
        assertThat(newMixtape).isEqualTo(mockMixtape);
        assertThat(newMixtape.getCreator()).isEqualTo(mockProfile);
        assertThat(newMixtape.getPlaylistId()).isEqualTo(mockPlaylistId);
        assertThat(newMixtape.getDurationMS()).isEqualTo(4000);
        assertThat(mockPlaylist.getMixtapes()).contains(mockMixtape);

        verify(mockPlaylistService).findPlaylist(mockPlaylistId);
        verify(mockTrackService).getMixtapeDuration(mockMixtape);
        verify(mockTrackService).inflateMixtape(mockMixtape);
        verify(mockPlaylistService).savePlaylist(mockPlaylist);
        verify(mockMixtapeRepository).save(mockMixtape);
        verify(mockNotificationService).createNotificationFromTrigger("mixtapeId1", mockProfile, mockTarget, "name1 created a mixtape mixtape1 for your shared playlist playlist1", NotificationType.MIXTAPE, mockPlaylistId);
    }

    @Test
    void createOrUpdateReactionForMixtape_doesNothing_whenReactionAlreadyExists() {
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
    void createOrUpdateReactionForMixtape_createsNewReaction_forNewReactionFromUser() {
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
    void createOrUpdateReactionForMixtape_updatesExistingReaction_forExistingReaction() {
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

    @Test
    void enqueueMixtape_enqueuesSongButDoesntMarkListened_whenCreator() {
        Profile creatingUser = new Profile("1", null, "user1", null);
        Mixtape mixtape = new Mixtape();
        mixtape.setCreator(creatingUser);
        mixtape.setSongIDs(List.of("song-1", "song-2"));

        when(mockMixtapeRepository.findById(any())).thenReturn(Optional.of(mixtape));

        mixtapeService.enqueueMixtape("1", creatingUser, "provider-token");

        verify(mockSpotifyService).enqueueSongs("provider-token", List.of("song-1", "song-2"));
        verify(mockMixtapeRepository, never()).save(any());
    }

    @Test
    void enqueueMixtape_enqueuesSongAndMarksAsListened_whenNotCreator() {
        Profile creatingUser = new Profile("1", null, "user1", null);
        Profile enqueueingUser = new Profile("2", null, "user2", null);
        Mixtape mixtape = new Mixtape();
        mixtape.setCreator(creatingUser);
        mixtape.setSongIDs(List.of("song-1", "song-2"));

        when(mockMixtapeRepository.findById(any())).thenReturn(Optional.of(mixtape));

        mixtapeService.enqueueMixtape("1", enqueueingUser, "provider-token");

        verify(mockSpotifyService).enqueueSongs("provider-token", List.of("song-1", "song-2"));
        verify(mockMixtapeRepository).save(mixtape);
    }

    @Test
    void enqueueMixtape_doesntMarkAsListened_whenEnqueueFails() {
        Profile creatingUser = new Profile("1", null, "user1", null);
        Profile enqueueingUser = new Profile("2", null, "user2", null);
        Mixtape mixtape = new Mixtape();
        mixtape.setCreator(creatingUser);
        mixtape.setSongIDs(List.of("song-1", "song-2"));

        when(mockMixtapeRepository.findById(any())).thenReturn(Optional.of(mixtape));

        doThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "invalid")).when(mockSpotifyService).enqueueSongs(any(), any());

        assertThatThrownBy(() -> {
            mixtapeService.enqueueMixtape("1", enqueueingUser, "provider-token");
        })
                .isInstanceOf(ResponseStatusException.class);

        verify(mockSpotifyService).enqueueSongs("provider-token", List.of("song-1", "song-2"));
        verify(mockMixtapeRepository, never()).save(mixtape);
    }
}
