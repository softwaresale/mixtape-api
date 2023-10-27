package com.mixtape.mixtapeapi.tracks;

import com.mixtape.mixtapeapi.mixtape.Mixtape;
import org.apache.hc.core5.http.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.tracks.GetSeveralTracksRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrackServiceTest {

    @Mock
    SpotifyApi mockSpotifyApi;

    TrackService trackService;

    @BeforeEach
    void setUp() {
        trackService = new TrackService(mockSpotifyApi);
    }

    @Test
    void getMixtapeDuration_getFullDuration_whenSuccess() throws IOException, ParseException, SpotifyWebApiException {
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
        Mixtape mockMixtape = new Mixtape();
        mockMixtape.setSongIDs(new ArrayList<>(songIds));

        Duration duration = trackService.getMixtapeDuration(mockMixtape);
        assertThat(duration).hasMillis(300);
        verify(mockSpotifyApi).getSeveralTracks("song-1", "song-2");
    }
}
