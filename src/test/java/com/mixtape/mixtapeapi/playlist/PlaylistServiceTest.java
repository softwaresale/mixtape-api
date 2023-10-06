package com.mixtape.mixtapeapi.playlist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlaylistServiceTest {
    @Mock
    PlaylistRepository mockPlaylistRepository;

    PlaylistService playlistService;

    @BeforeEach
    void beforeEach() {
        playlistService = new PlaylistService(mockPlaylistRepository);
    }

    @Test
    void updatePlaylist_exists() {
        Playlist playlist = new Playlist();
        String id = "id1";

        when(mockPlaylistRepository.existsById(id)).thenReturn(true);
        when(mockPlaylistRepository.save(playlist)).thenReturn(playlist);

        Optional<Playlist> retVal = playlistService.updatePlaylist(playlist, id);

        assertTrue(retVal.isPresent());
        assertThat(retVal.get()).isEqualTo(playlist);

        verify(mockPlaylistRepository).existsById(id);
        verify(mockPlaylistRepository).save(playlist);
    }

    @Test
    void updatePlaylist_doesNotExist() {
        Playlist playlist = new Playlist();
        String id = "id1";

        when(mockPlaylistRepository.existsById(id)).thenReturn(false);

        Optional<Playlist> retVal = playlistService.updatePlaylist(playlist, id);

        assertTrue(retVal.isEmpty());

        verify(mockPlaylistRepository).existsById(id);

    }

}
