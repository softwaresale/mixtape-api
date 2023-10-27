package com.mixtape.mixtapeapi.tracks;

import com.mixtape.mixtapeapi.mixtape.Mixtape;
import com.mixtape.mixtapeapi.playlist.Playlist;
import org.apache.hc.core5.http.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrackService {

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
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to perform spotify network request");
        }
    }

    public Playlist inflatePlaylist(Playlist playlist) throws IOException {
        List<Mixtape> mixtapes = playlist.getMixtapes();
        for (var mixtape : mixtapes) {
            List<TrackInfo> trackInfos = getTrackInfoForMixtape(mixtape);
            mixtape.setSongs(trackInfos);
        }
        playlist.setMixtapes(mixtapes);
        return playlist;
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
