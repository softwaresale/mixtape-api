package com.mixtape.mixtapeapi.spotify;

import com.mixtape.mixtapeapi.tracks.TrackInfo;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.miscellaneous.Device;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProdSpotifyService implements SpotifyService {

    private static final Logger logger = LoggerFactory.getLogger(ProdSpotifyService.class);

    private final SpotifyApi spotifyApi;
    private Instant tokenExpiresAt;

    public ProdSpotifyService(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
        this.tokenExpiresAt = null;
    }

    @Override
    public List<TrackInfo> getTrackInfos(String... ids) throws ResponseStatusException {
        if (tokenExpired()) {
            refreshToken();
        }

        try {
            Track[] spotifyTracks = spotifyApi.getSeveralTracks(ids)
                    .build()
                    .execute();

            return Arrays.stream(spotifyTracks)
                    .map(this::convertSpotifyTrack)
                    .toList();
        } catch (IOException | ParseException | SpotifyWebApiException spotifyExe) {
            logger.error("Failed to get track info for mixtape", spotifyExe);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to perform spotify network request", spotifyExe);
        }
    }

    @Override
    public Duration getTracksDuration(String... ids) throws ResponseStatusException {
        if (tokenExpired()) {
            refreshToken();
        }

        try {
            Track[] spotifyTracks = spotifyApi.getSeveralTracks(ids)
                    .build()
                    .execute();

            long totalDurationMs = Arrays.stream(spotifyTracks)
                    .mapToLong(Track::getDurationMs)
                    .sum();

            return Duration.ofMillis(totalDurationMs);
        } catch (IOException | ParseException | SpotifyWebApiException spotifyExe) {
            logger.error("Failed to get mixtape duration", spotifyExe);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to perform spotify network request", spotifyExe);
        }
    }

    private boolean tokenExpired() {
        return tokenExpiresAt == null || Instant.now().compareTo(tokenExpiresAt) > 0;
    }

    private void refreshToken() throws ResponseStatusException {
        try {
            ClientCredentials clientCredentials = spotifyApi.clientCredentials()
                    .build()
                    .execute();

            // set the access token
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
            // check when it's going to refresh
            tokenExpiresAt = Instant.now().plus(Duration.ofSeconds(clientCredentials.getExpiresIn()));
        } catch (IOException | ParseException | SpotifyWebApiException exe) {
            logger.error("Failed to refresh token", exe);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to refresh token", exe);
        }
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

    // TESTING ONLY
    protected void setTokenExpiresAt(Instant instant) {
        this.tokenExpiresAt = instant;
    }

    public Instant getTokenExpiresAt() {
        return tokenExpiresAt;
    }

    @Override
    public void enqueueSongs(String token, List<String> ids) throws ResponseStatusException {
        // Save the token state
        String savedAccessToken = this.spotifyApi.getAccessToken();
        this.spotifyApi.setAccessToken(token);

        // figure out what device to enqueue to
        Optional<Device> toEnqueueDevice = getDeviceToEnqueueTo(getUserDevices(token));

        for (String id : ids) {
            String uri = formatSpotifyUriForTrack(id);
            try {
                var builder = this.spotifyApi.addItemToUsersPlaybackQueue(uri);
                toEnqueueDevice.ifPresent(dev -> {
                    logger.info("Enqueuing mixtape to device {} (id={})", dev.getName(), dev.getId());
                    builder.device_id(dev.getId());
                });

                builder
                        .build()
                        .execute();
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                logger.error("Error while enqueueing song for user", e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to perform spotify request", e);
            }
        }

        this.spotifyApi.setAccessToken(savedAccessToken);
    }

    private List<Device> getUserDevices(String token) throws ResponseStatusException {
        String savedAccessToken = this.spotifyApi.getAccessToken();
        this.spotifyApi.setAccessToken(token);

        List<Device> devices;

        try {
            Device[] userDevices = this.spotifyApi.getUsersAvailableDevices()
                    .build()
                    .execute();

            devices = List.of(userDevices);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            logger.error("Error while fetching user devices", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to perform spotify request", e);
        }

        this.spotifyApi.setAccessToken(savedAccessToken);
        return devices;
    }

    private static Optional<Device> getDeviceToEnqueueTo(List<Device> devices) {
        // find the first one that's active
        Optional<Device> firstActive = devices.stream()
                .filter(device -> !device.getIs_restricted())
                .filter(Device::getIs_active)
                .findFirst();

        Optional<Device> firstPhone = devices.stream()
                .filter(device -> device.getType().equals("smartphone"))
                .findFirst();

        return firstActive.or(() -> firstPhone);
    }

    private String formatSpotifyUriForTrack(String trackId) {
        return String.format("spotify:track:%s", trackId);
    }
}
