package com.mixtape.mixtapeapi.search;

import com.mixtape.mixtapeapi.spotify.SpotifyService;
import com.mixtape.mixtapeapi.tracks.TrackInfo;
import com.mixtape.mixtapeapi.tracks.TrackService;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

import java.util.List;

@Service
public class SearchService {
    private final TrackService trackService;

    public SearchService(TrackService trackService) {
        this.trackService = trackService;
    }

    public List<TrackInfo> findRecentlyListenedToTracks(String providerToken) {
        return trackService.getRecentlyListenedTracks(providerToken);
    }
}
