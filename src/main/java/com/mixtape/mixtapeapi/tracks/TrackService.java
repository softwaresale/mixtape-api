package com.mixtape.mixtapeapi.tracks;

import com.mixtape.mixtapeapi.mixtape.Mixtape;
import com.mixtape.mixtapeapi.playlist.Playlist;
import com.mixtape.mixtapeapi.spotify.SpotifyService;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrackService {

    private static final Logger logger = LoggerFactory.getLogger(TrackService.class);

    private final SpotifyService spotifyService;

    public TrackService(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    public List<TrackInfo> getTrackInfoForMixtape(Mixtape tape) {
        // save the call on empty, but this shouldn't really happen ever...
        if (tape.getSongIDs().isEmpty()) {
            return List.of();
        }

        return spotifyService.getTrackInfos(tape.getSongIDs().toArray(String[]::new));
    }

    public Playlist inflatePlaylist(Playlist playlist) {
        // Grab mixtapes
        List<Mixtape> mixtapes = playlist.getMixtapes();

        // Inflate
        mixtapes = inflateMixtapes(mixtapes);

        // Set and return
        playlist.setMixtapes(mixtapes);
        return playlist;
    }

    public Mixtape inflateMixtape(Mixtape mixtape) {
        List<TrackInfo> trackInfos = getTrackInfoForMixtape(mixtape);
        mixtape.setSongs(trackInfos);
        return mixtape;
    }

    public List<Mixtape> inflateMixtapes(List<Mixtape> mixtapes) {
        // Fill mixtape
        for (Mixtape mixtape : mixtapes) {
            List<TrackInfo> trackInfos = getTrackInfoForMixtape(mixtape);
            mixtape.setSongs(trackInfos);
        }

        // Return
        return mixtapes;
    }

    public Duration getMixtapeDuration(Mixtape mixtape) {
        return spotifyService.getTracksDuration(mixtape.getSongIDs().toArray(String[]::new));
    }
}
