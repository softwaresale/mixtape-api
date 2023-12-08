package com.mixtape.mixtapeapi.tracks;

import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TrackInfo {

    public static TrackInfo fromSpotifyTrack(Track track) {
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

    private String id;
    private String name;
    private List<String> artistNames;
    private String albumName;
    private String albumImageURL;

    public TrackInfo() {
    }

    public TrackInfo(String id, String name, List<String> artistNames, String albumName, String albumImageURL) {
        this.id = id;
        this.name = name;
        this.artistNames = artistNames;
        this.albumName = albumName;
        this.albumImageURL = albumImageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getArtistNames() {
        return artistNames;
    }

    public void setArtistNames(List<String> artistNames) {
        this.artistNames = artistNames;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumImageURL() {
        return albumImageURL;
    }

    public void setAlbumImageURL(String albumImageURL) {
        this.albumImageURL = albumImageURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackInfo trackInfo = (TrackInfo) o;
        return Objects.equals(id, trackInfo.id) && Objects.equals(name, trackInfo.name) && Objects.equals(artistNames, trackInfo.artistNames) && Objects.equals(albumName, trackInfo.albumName) && Objects.equals(albumImageURL, trackInfo.albumImageURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, artistNames, albumName, albumImageURL);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TrackInfo{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", artistNames=").append(artistNames);
        sb.append(", albumName='").append(albumName).append('\'');
        sb.append(", albumImageURL='").append(albumImageURL).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
