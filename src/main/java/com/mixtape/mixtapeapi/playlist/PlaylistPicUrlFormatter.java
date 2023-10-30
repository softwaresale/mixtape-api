package com.mixtape.mixtapeapi.playlist;

public class PlaylistPicUrlFormatter {

    private final String baseURL;

    public PlaylistPicUrlFormatter(String baseURL) {
        this.baseURL = baseURL;
    }

    public String formatObjectId(String playlistId, String fileName) {
        return String.format("%s-%s", playlistId, fileName);
    }

    public String formatPlaylistPicURL(String playlistId, String fileName) {
        String objectId = this.formatObjectId(playlistId, fileName);
        return String.format("%s/%s", baseURL, objectId);
    }
}
