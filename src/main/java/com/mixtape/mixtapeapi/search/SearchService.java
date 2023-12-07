package com.mixtape.mixtapeapi.search;

import com.mixtape.mixtapeapi.spotify.SpotifyService;
import com.mixtape.mixtapeapi.tracks.TrackInfo;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

import java.util.List;

@Service
public class SearchService {
    private final SpotifyService spotifyService;

    public SearchService(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    public List<TrackInfo> findRecentlyListenedToTracks(String providerToken) {
        return spotifyService.getRecentlyListenedToTracks(providerToken);
    }
}
