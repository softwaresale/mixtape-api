package com.mixtape.mixtapeapi.spotify;

import com.mixtape.mixtapeapi.tracks.TrackInfo;
import org.springframework.web.server.ResponseStatusException;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

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

    void enqueueSongs(String token, List<String> ids) throws ResponseStatusException;

    /**
     * Check if a user follows any of the users given by their IDs.
     * @param userProviderToken Provider token of the user making the request
     * @param spotifyUserIDs List of spotify user IDs to check
     * @return A list of spotify user ids that the user follows
     * @throws ResponseStatusException An HTTP response exception if there is an error
     */
    List<String> checkFollowsAnyUsers(String userProviderToken, List<String> spotifyUserIDs) throws ResponseStatusException;

    List<TrackSimplified> getRecentlyListenedToTracks(String providerToken);
}
