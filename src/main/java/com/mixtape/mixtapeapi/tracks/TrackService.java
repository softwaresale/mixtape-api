package com.mixtape.mixtapeapi.tracks;

import com.mixtape.mixtapeapi.mixtape.Mixtape;
import com.mixtape.mixtapeapi.playlist.Playlist;
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

    private final SpotifyApi spotifyApi;

    public TrackService(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
    }

    public List<TrackInfo> getTrackInfoForMixtape(Mixtape tape) throws IOException {
        try {
            Track[] spotifyTracks = spotifyApi.getSeveralTracks(tape.getSongIDs().toArray(String[]::new))
                    .build()
                    .execute();

            return Arrays.stream(spotifyTracks)
                    .map(this::convertSpotifyTrack)
                    .toList();
        } catch (ParseException | SpotifyWebApiException spotifyExe) {
            logger.error("Failed to get track info for mixtape", spotifyExe);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to perform spotify network request", spotifyExe);
        }
    }

    public Playlist inflatePlaylist(Playlist playlist) throws IOException {
        // Grab mixtapes
        List<Mixtape> mixtapes = playlist.getMixtapes();

        // Inflate
        mixtapes = inflateMixtapes(mixtapes);

        // Set and return
        playlist.setMixtapes(mixtapes);
        return playlist;
    }

    public Mixtape inflateMixtape(Mixtape mixtape) throws IOException {
        List<TrackInfo> trackInfos = getTrackInfoForMixtape(mixtape);
        mixtape.setSongs(trackInfos);
        return mixtape;
    }

    public List<Mixtape> inflateMixtapes(List<Mixtape> mixtapes) throws IOException {
        // Fill mixtape
        for (Mixtape mixtape : mixtapes) {
            List<TrackInfo> trackInfos = getTrackInfoForMixtape(mixtape);
            mixtape.setSongs(trackInfos);
        }

        // Return
        return mixtapes;
    }

    public Duration getMixtapeDuration(Mixtape mixtape) throws IOException {
        try {
            Track[] spotifyTracks = spotifyApi.getSeveralTracks(mixtape.getSongIDs().toArray(String[]::new))
                    .build()
                    .execute();

            long totalDurationMs = Arrays.stream(spotifyTracks)
                    .mapToLong(Track::getDurationMs)
                    .sum();

            return Duration.ofMillis(totalDurationMs);
        } catch (ParseException | SpotifyWebApiException spotifyExe) {
            logger.error("Failed to get mixtape duration", spotifyExe);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to perform spotify network request", spotifyExe);
        }
    }

    private TrackInfo convertSpotifyTrack(Track track) {
        String albumName = track.getAlbum().getName();
        List<String> artistNames = Arrays.stream(track.getArtists())
                .map(ArtistSimplified::getName)
                .collect(Collectors.toList());

        String albumURL = Arrays.stream(track.getAlbum().getImages())
                .max(Comparator.comparingInt(Image::getHeight))
                .map(Image::getUrl)
                .orElse("");

        return new TrackInfo(track.getId(), track.getName(), artistNames, albumName, albumURL);
    }
}
