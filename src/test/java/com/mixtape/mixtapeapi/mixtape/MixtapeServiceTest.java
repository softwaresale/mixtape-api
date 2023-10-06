package com.mixtape.mixtapeapi.mixtape;

import com.mixtape.mixtapeapi.playlist.PlaylistService;
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
public class MixtapeServiceTest {
    @Mock
    MixtapeRepository mockMixtapeRepository;

    @Mock
    PlaylistService mockPlaylistService;

    MixtapeService mixtapeService;

    @BeforeEach
    void beforeEach() {
        mixtapeService = new MixtapeService(mockMixtapeRepository, mockPlaylistService);
    }

    @Test
    void getByIds_mixtapeExistsCorrectPlaylist() {
        String playlistId = "playlistId";
        String mixtapeId = "mixtapeId";
        Mixtape mixtape = new Mixtape(null, playlistId, null, null, null, null, null);
        Optional<Mixtape> optionalMixtape = Optional.of(mixtape);

        when(mockMixtapeRepository.findById(mixtapeId)).thenReturn(optionalMixtape);

        Optional<Mixtape> retVal = mixtapeService.getByIds(playlistId, mixtapeId);
        assertTrue(retVal.isPresent());
        assertThat(retVal.get()).isEqualTo(mixtape);

        verify(mockMixtapeRepository).findById(mixtapeId);
    }

    @Test
    void getByIds_mixtapeExistWrongPlaylist() {
        String playlistId = "playlistId";
        String wrongPlaylistId = "wrongPlaylistId";
        String mixtapeId = "mixtapeId";
        Mixtape mixtape = new Mixtape(null, playlistId, null, null, null, null, null);
        Optional<Mixtape> optionalMixtape = Optional.of(mixtape);

        when(mockMixtapeRepository.findById(mixtapeId)).thenReturn(optionalMixtape);

        Optional<Mixtape> retVal = mixtapeService.getByIds(wrongPlaylistId, mixtapeId);
        assertTrue(retVal.isEmpty());

        verify(mockMixtapeRepository).findById(mixtapeId);
    }

    @Test
    void getByIds_mixtapeDoesNotExist() {
        String playlistId = "playlistId";
        String mixtapeId = "mixtapeId";

        when(mockMixtapeRepository.findById(mixtapeId)).thenReturn(Optional.empty());

        Optional<Mixtape> retVal = mixtapeService.getByIds(playlistId, mixtapeId);
        assertTrue(retVal.isEmpty());

        verify(mockMixtapeRepository).findById(mixtapeId);
    }


}
