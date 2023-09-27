package com.mixtape.mixtapeapi.search;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public List<Track> getTracksByQueryParam(@RequestParam String artistName, @RequestParam String albumName, @RequestParam String playlistName) {
        if (artistName != null && albumName == null && playlistName == null) {
            return searchService.findTracksByArtistName(artistName)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        } else if (artistName == null && albumName != null && playlistName == null) {
            return searchService.findTracksByAlbumName(albumName)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        } else if (artistName == null && albumName == null && playlistName != null) {
            return searchService.findTracksByPlaylistName(playlistName)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
