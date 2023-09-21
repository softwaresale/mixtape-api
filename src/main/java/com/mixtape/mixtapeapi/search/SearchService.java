package com.mixtape.mixtapeapi.search;

import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;

import java.util.List;

@Service
public class SearchService {
    private final SpotifyApi spotifyApi;

    public SearchService(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
    }

    public List<String> getUsersByName(String name) {
        // TODO: Not sure if feasible
        return null;
    }

}
