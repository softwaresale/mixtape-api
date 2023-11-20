package com.mixtape.mixtapeapi.search;

import com.mixtape.mixtapeapi.spotify.SpotifyService;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

import java.util.List;

@Service
public class SearchService {
    private final SpotifyService spotifyService;

    public SearchService(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    public List<TrackSimplified> findRecentlyListenedToTracks(String providerToken) {
        return spotifyService.getRecentlyListenedToTracks(providerToken);
    }
}
