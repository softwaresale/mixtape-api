package com.mixtape.mixtapeapi.search;

import org.apache.hc.core5.http.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public List<String> getUsers(@RequestParam String userName) {
        return searchService.getUsersByName(userName);
    }

    @GetMapping
    public List<Track> getTracksByArtist(@RequestParam String artistName) {
        try {
            return searchService.findTracksByArtistName(artistName);
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping
    public List<Track> getTracksByAlbum(@RequestParam String albumName) {
        try {
            return searchService.findTracksByAlbumName(albumName);
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping
    public List<Track> getTracksByPlaylist(@RequestParam String playlistName) {
        try {
            return searchService.findTracksByPlaylistName(playlistName);
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
    }
}
