package com.mixtape.mixtapeapi.spotify;

import com.mixtape.mixtapeapi.tracks.TrackInfo;
import org.apache.hc.core5.http.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetSeveralTracksRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProdSpotifyServiceTest {

    @Mock
    ClientCredentialsRequest mockClientCredentialsRequest;

    @Mock
    ClientCredentialsRequest.Builder clientCredentialsBuilder;

    @Mock
    SpotifyApi mockSpotifyApi;

    ProdSpotifyService spotifyService;

    @BeforeEach
    void setUp() {
        spotifyService = new ProdSpotifyService(mockSpotifyApi);
    }

    @Test
    void getTrackInfos_success_whenNonExpiredTokenAndValidRequest() throws IOException, ParseException, SpotifyWebApiException {
        spotifyService.setTokenExpiresAt(Instant.now().plus(Duration.ofHours(1)));

        Track[] tracks = {
                new Track.Builder()
                        .setId("song-1")
                        .setDurationMs(100)
                        .setName("song-1")
                        .setAlbum(new AlbumSimplified.Builder().setName("album-1").setImages().build())
                        .setArtists(new ArtistSimplified.Builder().setName("artist-1").build(), new ArtistSimplified.Builder().setName("artist-2").build())
                        .build(),
        };

        List<String> songIds = List.of("song-1");
        String[] songIdsArray = songIds.toArray(String[]::new);
        GetSeveralTracksRequest mockRequest = mock(GetSeveralTracksRequest.class);
        when(mockRequest.execute()).thenReturn(tracks);
        GetSeveralTracksRequest.Builder mockBuilder = mock(GetSeveralTracksRequest.Builder.class);
        when(mockBuilder.build()).thenReturn(mockRequest);
        when(mockSpotifyApi.getSeveralTracks(songIdsArray)).thenReturn(mockBuilder);

        List<TrackInfo> trackInfos = spotifyService.getTrackInfos(songIdsArray);
        assertThat(trackInfos).contains(new TrackInfo("song-1", "song-1", List.of("artist-1", "artist-2"), "album-1", ""));
    }

    @Test
    void getTrackInfos_successfully_pullsTokenWhenExpired() throws IOException, ParseException, SpotifyWebApiException {
        when(clientCredentialsBuilder.build()).thenReturn(mockClientCredentialsRequest);
        when(mockSpotifyApi.clientCredentials()).thenReturn(clientCredentialsBuilder);
        ClientCredentials clientCredentials = new ClientCredentials.Builder()
                .setAccessToken("access-token")
                .setExpiresIn(10)
                .build();
        when(mockClientCredentialsRequest.execute()).thenReturn(clientCredentials);

        Track[] tracks = {
                new Track.Builder()
                        .setId("song-1")
                        .setDurationMs(100)
                        .setName("song-1")
                        .setAlbum(new AlbumSimplified.Builder().setName("album-1").setImages().build())
                        .setArtists(new ArtistSimplified.Builder().setName("artist-1").build(), new ArtistSimplified.Builder().setName("artist-2").build())
                        .build(),
        };

        List<String> songIds = List.of("song-1");
        String[] songIdsArray = songIds.toArray(String[]::new);
        GetSeveralTracksRequest mockRequest = mock(GetSeveralTracksRequest.class);
        when(mockRequest.execute()).thenReturn(tracks);
        GetSeveralTracksRequest.Builder mockBuilder = mock(GetSeveralTracksRequest.Builder.class);
        when(mockBuilder.build()).thenReturn(mockRequest);
        when(mockSpotifyApi.getSeveralTracks(songIdsArray)).thenReturn(mockBuilder);

        spotifyService.getTrackInfos("song-1");
        verify(mockSpotifyApi).setAccessToken("access-token");
        assertThat(spotifyService.getTokenExpiresAt()).isBefore(Instant.now().plus(Duration.ofMinutes(10)));
    }

    @Test
    void getTrackInfos_throwsResponseStatusException_whenFetchingTokenFails() throws IOException, ParseException, SpotifyWebApiException {
        when(clientCredentialsBuilder.build()).thenReturn(mockClientCredentialsRequest);
        when(mockSpotifyApi.clientCredentials()).thenReturn(clientCredentialsBuilder);
        ClientCredentials clientCredentials = new ClientCredentials.Builder()
                .setAccessToken("access-token")
                .setExpiresIn(10)
                .build();
        when(mockClientCredentialsRequest.execute()).thenThrow(new IOException());

        assertThatThrownBy(() -> {
            spotifyService.getTrackInfos("song-1");
        })
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void getTracksDuration_getFullDuration_whenSuccessAndHasNonExpiredToken() throws IOException, ParseException, SpotifyWebApiException {

        // simulate having a non-expired token
        spotifyService.setTokenExpiresAt(Instant.now().plus(Duration.ofHours(1)));

        Track[] tracks = {
                new Track.Builder()
                        .setDurationMs(100)
                        .build(),
                new Track.Builder()
                        .setDurationMs(200)
                        .build()
        };

        List<String> songIds = List.of("song-1", "song-2");
        String[] songIdsArray = songIds.toArray(String[]::new);
        GetSeveralTracksRequest mockRequest = mock(GetSeveralTracksRequest.class);
        when(mockRequest.execute()).thenReturn(tracks);
        GetSeveralTracksRequest.Builder mockBuilder = mock(GetSeveralTracksRequest.Builder.class);
        when(mockBuilder.build()).thenReturn(mockRequest);
        when(mockSpotifyApi.getSeveralTracks(songIdsArray)).thenReturn(mockBuilder);

        Duration duration = spotifyService.getTracksDuration(songIdsArray);
        assertThat(duration).hasMillis(300);
        verify(mockSpotifyApi).getSeveralTracks("song-1", "song-2");
    }
}
