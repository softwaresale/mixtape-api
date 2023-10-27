package com.mixtape.mixtapeapi.tracks;

import java.util.List;

public class TrackInfo {
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
}
