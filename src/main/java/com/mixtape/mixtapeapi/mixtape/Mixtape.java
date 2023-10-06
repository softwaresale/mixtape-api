package com.mixtape.mixtapeapi.mixtape;

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
    private String creatorID;
    @ElementCollection
    private List<String> songIDs;

    public Mixtape() {
    }

    public Mixtape(String id, String playlistID, String name, LocalDateTime createdAt, String description, String creatorID, List<String> songIDs) {
        this.id = id;
        this.playlistID = playlistID;
        this.name = name;
        this.createdAt = createdAt;
        this.description = description;
        this.creatorID = creatorID;
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

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
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
}
