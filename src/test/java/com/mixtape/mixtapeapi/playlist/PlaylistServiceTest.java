package com.mixtape.mixtapeapi.playlist;

import com.mixtape.mixtapeapi.mixtape.Mixtape;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.tracks.TrackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlaylistServiceTest {

    @Mock PlaylistRepository mockRepository;
    @Mock
    TrackService mockTrackService;

    PlaylistService playlistService;

    @BeforeEach
    void beforeEach() {
        playlistService = new PlaylistService(mockRepository, mockTrackService);
    }

    @Test
    void findPlaylistForProfile_returnsEmpty_whenQueryFindsNothing() throws IOException {
        when(mockRepository.findByIdAndInitiatorOrTarget(any(), any(), any())).thenReturn(Optional.empty());

        Optional<Playlist> result = playlistService.findPlaylistForProfile(new Profile(), "");

        assertThat(result).isEmpty();
        verify(mockTrackService, never()).inflatePlaylist(any());
    }

    @Test
    void findPlaylistForProfile_inflatesPlaylist_whenQueryFindsResult() throws IOException {
        Playlist mockPlaylist = new Playlist();
        mockPlaylist.setName("my playlist");
        Mixtape sampleMixtape = new Mixtape();
        sampleMixtape.setName("my mixtape");
        sampleMixtape.setSongIDs(List.of("song-id-1"));
        mockPlaylist.setMixtapes(List.of(sampleMixtape));

        when(mockRepository.findByIdAndInitiatorOrTarget(any(), any(), any())).thenReturn(Optional.of(mockPlaylist));
        when(mockTrackService.inflatePlaylist(mockPlaylist)).thenReturn(mockPlaylist);

        Optional<Playlist> result = playlistService.findPlaylistForProfile(new Profile(), "");

        assertThat(result).hasValue(mockPlaylist);
        verify(mockTrackService).inflatePlaylist(mockPlaylist);
    }

    @Test
    void findPlaylistForProfile_throwsInternalServerError_whenFails() throws IOException {
        Playlist mockPlaylist = new Playlist();
        when(mockRepository.findByIdAndInitiatorOrTarget(any(), any(), any())).thenReturn(Optional.of(mockPlaylist));
        when(mockTrackService.inflatePlaylist(mockPlaylist)).thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));

        var result = assertThatThrownBy(() -> {
            playlistService.findPlaylistForProfile(new Profile(), "");
        });
        result.isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void updatePlaylist_exists() {
        Playlist playlist = new Playlist();
        String id = "id1";

        when(mockRepository.existsById(id)).thenReturn(true);
        when(mockRepository.save(playlist)).thenReturn(playlist);

        Optional<Playlist> retVal = playlistService.updatePlaylist(playlist, id);

        assertThat(retVal).hasValue(playlist);

        verify(mockRepository).existsById(id);
        verify(mockRepository).save(playlist);
    }

    @Test
    void updatePlaylist_doesNotExist() {
        Playlist playlist = new Playlist();
        String id = "id1";

        when(mockRepository.existsById(id)).thenReturn(false);

        Optional<Playlist> retVal = playlistService.updatePlaylist(playlist, id);

        assertThat(retVal).isEmpty();

        verify(mockRepository).existsById(id);

    }

}