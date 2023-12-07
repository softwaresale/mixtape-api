package com.mixtape.mixtapeapi.spotify;

import com.mixtape.mixtapeapi.tracks.TrackInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class MockSpotifyService implements SpotifyService {

    private static final Logger logger = LoggerFactory.getLogger(MockSpotifyService.class);

    @Override
    public List<TrackInfo> getTrackInfos(String... ids) throws ResponseStatusException {
        logger.info("Getting track infos for ids: {}", String.join(",", ids));
        return Arrays.stream(ids)
                .map(id -> new TrackInfo(id, String.format("song-%s", id), List.of("artist"), "album", ""))
                .toList();
    }

    @Override
    public Duration getTracksDuration(String... ids) throws ResponseStatusException {
        logger.info("Getting duration infos for ids: {}", String.join(",", ids));
        long ms = Arrays.stream(ids)
                .mapToInt(id -> 2000)
                .sum();

        return Duration.ofMillis(ms);
    }

    @Override
    public void enqueueSongs(String token, List<String> ids) throws ResponseStatusException {
        logger.info("Enqueued songs: {}", ids);
    }

    @Override
    public List<String> checkFollowsAnyUsers(String userProviderToken, List<String> spotifyUserIDs) throws ResponseStatusException {
        logger.info("Checking user follows {}", spotifyUserIDs);
        return spotifyUserIDs;
    }

    @Override
    public List<TrackSimplified> getRecentlyListenedToTracks(String providerToken) {
        return List.of();
    }
}
