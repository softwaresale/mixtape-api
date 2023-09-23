package com.mixtape.mixtapeapi.search;

import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.special.SearchResult;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class SearchService {
    private final static int MAX_ITEMS = 50;
    private final SpotifyApi spotifyApi;

    public SearchService(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
    }

    public List<Track> findTracksByArtistName(String artistName) throws IOException, ParseException, SpotifyWebApiException {
        return getTracksByArtistOrAlbum(artistName, true);
    }

    public List<Track> findTracksByAlbumName(String albumName) throws IOException, ParseException, SpotifyWebApiException {
        return getTracksByArtistOrAlbum(albumName, false);
    }

    public List<Track> findTracksByPlaylistName(String playlistName) throws IOException, ParseException, SpotifyWebApiException {
        // Setup paging request
        Paging<PlaylistSimplified> pageOfPlaylist = spotifyApi
                .searchPlaylists(playlistName)
                .build()
                .execute();

        // Check if more than one result
        if (pageOfPlaylist.getTotal() != 1) {
            return Collections.emptyList();
        }

        // Grab id of playlist and create list of tracks and counter for requests
        String playlistId = pageOfPlaylist.getItems()[0].getId();
        List<Track> tracks = new ArrayList<>();
        int counter = 0;

        // Grab paging playlist tracks from spotify
        Paging<PlaylistTrack> pageOfPlaylistTracks = spotifyApi
                .getPlaylistsItems(playlistId)
                .limit(MAX_ITEMS)
                .offset(0)
                .build()
                .execute();

        // Grab all ids
        while (pageOfPlaylistTracks != null) {
            // Append to trackIds
            Arrays.asList(pageOfPlaylistTracks.getItems())
                    .forEach(playlistTrack -> tracks.add((Track) playlistTrack.getTrack()));

            // Update pageOfPlaylistTracks to null if none left or re-query
            pageOfPlaylistTracks = pageOfPlaylistTracks.getNext() == null ?
                    null :
                    spotifyApi
                            .getPlaylistsItems(playlistId)
                            .limit(MAX_ITEMS)
                            .offset(++counter * MAX_ITEMS)
                            .build()
                            .execute();
        }

        // Return all tracks found
        return tracks;
    }

    private List<Track> getTracksByArtistOrAlbum(String name, boolean artist) throws IOException, ParseException, SpotifyWebApiException {
        // Create query string, offset counter, and list to return
        String query = String.format("%%20%s%s", artist ? "artist" : "album", name);
        int counter = 0;
        List<Track> tracks = new ArrayList<>();

        // Setup paging request
        Paging<Track> pageOfTracks = spotifyApi
                .searchTracks(query)
                .limit(MAX_ITEMS)
                .offset(0)
                .build()
                .execute();

        // Request until complete
        while (pageOfTracks != null) {
            // Append to tracks
            Arrays.asList(pageOfTracks.getItems())
                    .forEach(tracks::add);

            // Update pageOfTracks to null if none left or re-query
            pageOfTracks = pageOfTracks.getNext() == null ?
                    null :
                    getNextItemSet(query, ModelObjectType.TRACK.getType(), ++counter).getTracks();
        }

        // Return filled out tracks
        return tracks;
    }

    private SearchResult getNextItemSet(String q, String itemType, int offset) throws IOException, ParseException, SpotifyWebApiException {
        return spotifyApi
                .searchItem(q, itemType)
                .offset(offset * MAX_ITEMS)
                .limit(MAX_ITEMS)
                .build()
                .execute();
    }

}
