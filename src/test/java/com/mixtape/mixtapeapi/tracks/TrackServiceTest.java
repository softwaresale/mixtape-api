package com.mixtape.mixtapeapi.tracks;

import com.mixtape.mixtapeapi.mixtape.Mixtape;
import com.mixtape.mixtapeapi.playlist.Playlist;
import com.mixtape.mixtapeapi.spotify.SpotifyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TrackServiceTest {

    @Mock
    SpotifyService mockSpotifyService;

    TrackService trackService;

    @BeforeEach
    void setUp() {
        trackService = new TrackService(mockSpotifyService);
    }

    @Test
    void getTrackInfoForMixtape_works_whenCalledWithMixtape() {
        Mixtape mixtape = new Mixtape();
        mixtape.setSongIDs(List.of("song-01", "song-02"));

        trackService.getTrackInfoForMixtape(mixtape);

        verify(mockSpotifyService).getTrackInfos("song-01", "song-02");
    }

    @Test
    void getTrackInfoForMixtape_doesNothing_whenNoSongs() {
        Mixtape mixtape = new Mixtape();

        trackService.getTrackInfoForMixtape(mixtape);

        verify(mockSpotifyService, never()).getTrackInfos();
    }

    @Test
    void inflatePlaylist_inflatesAllMixtapes_andSetsThem() {
        List<Mixtape> mixtapes = List.of(
                new Mixtape("1", "", "mixtape1", LocalDateTime.now(), "", 0L, null, new ArrayList<>(List.of("song-1", "song-2")), new ArrayList<>()),
                new Mixtape("2", "", "mixtape1", LocalDateTime.now(), "", 0L, null, new ArrayList<>(List.of("song-3", "song-4")), new ArrayList<>()),
                new Mixtape("3", "", "mixtape1", LocalDateTime.now(), "", 0L, null, new ArrayList<>(List.of("song-5", "song-6")), new ArrayList<>())
        );
        Playlist mockPlaylist = new Playlist();
        mockPlaylist.setMixtapes(mixtapes);

        trackService.inflatePlaylist(mockPlaylist);

        verify(mockSpotifyService).getTrackInfos("song-1", "song-2");
        verify(mockSpotifyService).getTrackInfos("song-3", "song-4");
        verify(mockSpotifyService).getTrackInfos("song-5", "song-6");
    }
}
