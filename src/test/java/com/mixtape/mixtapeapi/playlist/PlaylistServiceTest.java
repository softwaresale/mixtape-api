package com.mixtape.mixtapeapi.playlist;

import com.mixtape.mixtapeapi.mixtape.Mixtape;
import com.mixtape.mixtapeapi.notification.NotificationService;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.blocking.BlockedActionService;
import com.mixtape.mixtapeapi.tracks.TrackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkException;

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
    @Mock
    PlaylistPicUploadService mockPicUploadService;
    @Mock
    NotificationService mockNotificationService;
    @Mock
    BlockedActionService mockBlockedActionService;

    PlaylistService playlistService;

    @BeforeEach
    void beforeEach() {
        playlistService = new PlaylistService(mockRepository, mockNotificationService, mockTrackService, mockPicUploadService, mockBlockedActionService);
    }

    @Test
    void findPlaylistForProfile_returnsEmpty_whenQueryFindsNothing() throws IOException {
        when(mockRepository.findByIdAndInitiatorOrIdAndTarget(any(), any(), any(), any())).thenReturn(Optional.empty());

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

        when(mockRepository.findByIdAndInitiatorOrIdAndTarget(any(), any(), any(), any())).thenReturn(Optional.of(mockPlaylist));
        when(mockTrackService.inflatePlaylist(mockPlaylist)).thenReturn(mockPlaylist);

        Optional<Playlist> result = playlistService.findPlaylistForProfile(new Profile(), "");

        assertThat(result).hasValue(mockPlaylist);
        verify(mockTrackService).inflatePlaylist(mockPlaylist);
    }

    @Test
    void findPlaylistForProfile_throwsInternalServerError_whenFails() throws IOException {
        Playlist mockPlaylist = new Playlist();
        when(mockRepository.findByIdAndInitiatorOrIdAndTarget(any(), any(), any(), any())).thenReturn(Optional.of(mockPlaylist));
        when(mockTrackService.inflatePlaylist(mockPlaylist)).thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));

        var result = assertThatThrownBy(() -> {
            playlistService.findPlaylistForProfile(new Profile(), "");
        });
        result.isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void createPlaylist_succeeds_whenDataValid() {
        Profile initiator = new Profile();
        Profile target = new Profile();
        PlaylistDTO.Create createPlaylist = new PlaylistDTO.Create("playlist", "desc", "pic-url");

        when(mockRepository.save(any())).then((Answer<Playlist>) invocation -> {
            Playlist result = (Playlist) invocation.getArguments()[0];
            result.setId("pid");
            return result;
        });

        Playlist resultingPlaylist = playlistService.createPlaylist(initiator, createPlaylist, target);
        assertThat(resultingPlaylist).isNotNull();
        assertThat(resultingPlaylist.getName()).isEqualTo("playlist");
        assertThat(resultingPlaylist.getDescription()).isEqualTo("desc");
        assertThat(resultingPlaylist.getCoverPicURL()).isEqualTo("pic-url");
        assertThat(resultingPlaylist.getInitiator()).isEqualTo(initiator);
        assertThat(resultingPlaylist.getTarget()).isNull();

        verify(mockRepository).save(any());
    }

    @Test
    void createPlaylist_fails_whenBlocked() {
        Profile initiator = new Profile();
        Profile target = new Profile();
        PlaylistDTO.Create createPlaylist = new PlaylistDTO.Create("playlist", "desc", "pic-url");
        when(mockBlockedActionService.isBlockedSymmetrical(initiator, target)).thenReturn(true);
        assertThatThrownBy(() -> {
            playlistService.createPlaylist(initiator, createPlaylist, target);
        })
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void setPlaylistPicture_shouldUploadPicture_whenUploadSuccessful() throws IOException {
        Playlist playlist = new Playlist();
        String id = "id1";
        when(mockRepository.findById(id)).thenReturn(Optional.of(playlist));
        MockMultipartFile file = new MockMultipartFile("file", "image/png", "me.png", new byte[10]);
        when(mockPicUploadService.uploadPictureForPlaylist("id1", file)).thenReturn("some-pic-url");
        when(mockRepository.save(any())).then((Answer<Playlist>) invocation -> {
            Playlist result = (Playlist) invocation.getArguments()[0];
            result.setId("pid");
            return result;
        });

        Playlist updatedPlaylist = playlistService.setPlaylistPicture("id1", file);

        assertThat(updatedPlaylist.getCoverPicURL()).isEqualTo("some-pic-url");
        verify(mockPicUploadService).uploadPictureForPlaylist("id1", file);
        verify(mockRepository).save(playlist);
    }

    @Test
    void setPlaylistPicture_doesNothing_whenUploadFails() throws IOException {
        Playlist playlist = new Playlist();
        String id = "id1";
        when(mockRepository.findById(id)).thenReturn(Optional.of(playlist));
        MockMultipartFile file = new MockMultipartFile("file", "image/png", "me.png", new byte[10]);
        when(mockPicUploadService.uploadPictureForPlaylist("id1", file)).thenThrow(AwsServiceException.create("fail", null));

        assertThatThrownBy(() -> {
            playlistService.setPlaylistPicture("id1", file);
        }).isInstanceOf(SdkException.class);

        verify(mockPicUploadService).uploadPictureForPlaylist("id1", file);
        verify(mockRepository, never()).save(any());
    }
}
