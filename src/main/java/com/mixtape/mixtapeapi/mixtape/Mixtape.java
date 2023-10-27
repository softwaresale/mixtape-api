package com.mixtape.mixtapeapi.mixtape;

import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.tracks.TrackInfo;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Mixtape {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String playlistID;
    private String name;
    private LocalDateTime createdAt;
    private String description;

    @ManyToOne
    @JoinColumn(name="creator_id")
    private Profile creator;

    @ElementCollection
    private List<String> songIDs;

    @Transient
    private List<TrackInfo> songs;

    public Mixtape() {
    }

    public Mixtape(MixtapeDTO.Create createDTO) {
        this(null, null, createDTO.name, LocalDateTime.now(), createDTO.description, null, createDTO.songIDs);
    }

    public Mixtape(String id, String playlistID, String name, LocalDateTime createdAt, String description, Profile creator, List<String> songIDs) {
        this.id = id;
        this.playlistID = playlistID;
        this.name = name;
        this.createdAt = createdAt;
        this.description = description;
        this.creator = creator;
        this.songIDs = songIDs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaylistID() {
        return playlistID;
    }

    public void setPlaylistID(String playlistID) {
        this.playlistID = playlistID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Profile getCreator() {
        return creator;
    }

    public void setCreator(Profile creator) {
        this.creator = creator;
    }

    public List<String> getSongIDs() {
        return songIDs;
    }

    public void setSongIDs(List<String> songIDs) {
        this.songIDs = songIDs;
    }

    public void addSongID(String songID) {
        this.songIDs.add(songID);
    }

    public List<TrackInfo> getSongs() {
        return songs;
    }

    public void setSongs(List<TrackInfo> songs) {
        this.songs = songs;
    }
}
