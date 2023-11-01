package com.mixtape.mixtapeapi.spotify;

import com.mixtape.mixtapeapi.tracks.TrackInfo;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;

public interface SpotifyService {

    /**
     * Get a list of track infos from spotify. Note that this method DOES NOT let you throw checked exceptions. That is
     * on purpose. Catch your exceptions and throw a ResponseStatusException
     * @param ids Sequence of spotify ids to get
     * @return List of track infos
     * @throws ResponseStatusException An HTTP response exception that the user should see and make sense of
     */
    List<TrackInfo> getTrackInfos(String ...ids) throws ResponseStatusException;

    /**
     * Get the duration of one or more tracks
     * @param ids Sequence of spotify ids to query
     * @return The sum duration of all the ids provided
     * @throws ResponseStatusException An HTTP response exception that the user should see and make sense of
     */
    Duration getTracksDuration(String ...ids) throws ResponseStatusException;
}
