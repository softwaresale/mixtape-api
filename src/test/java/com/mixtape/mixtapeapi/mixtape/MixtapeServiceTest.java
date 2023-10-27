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

import java.io.IOException;
import java.time.Duration;
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

    @Mock
    TrackService mockTrackService;

    MixtapeService mixtapeService;

    @BeforeEach
    void beforeEach() {
        mixtapeService = new MixtapeService(mockMixtapeRepository, mockPlaylistService, mockTrackService);
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
}
