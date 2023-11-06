package com.mixtape.mixtapeapi.playlist;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mixtape.mixtapeapi.mixtape.Mixtape;
import com.mixtape.mixtapeapi.profile.Profile;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String spotifyID;
    private String name;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private Profile initiator;

    @ManyToOne
    @JoinColumn(name = "target_id")
    private Profile target;
    private String description;
    private String coverPicURL;

    @OneToMany(cascade = CascadeType.REMOVE)
    private List<Mixtape> mixtapes;

    public Playlist() {
        this("", "", "", null, null, "", "");
    }

    public Playlist(String id, String spotifyID, String name, Profile initiator, Profile target, String description, String coverPicURL) {
        this.id = id;
        this.spotifyID = spotifyID;
        this.name = name;
        this.initiator = initiator;
        this.target = target;
        this.description = description;
        this.coverPicURL = coverPicURL;
        this.mixtapes = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpotifyID() {
        return spotifyID;
    }

    public void setSpotifyID(String spotifyID) {
        this.spotifyID = spotifyID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Profile getInitiator() {
        return initiator;
    }

    public void setInitiator(Profile initiator) {
        this.initiator = initiator;
    }

    public Profile getTarget() {
        return target;
    }

    public void setTarget(Profile target) {
        this.target = target;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverPicURL() {
        return coverPicURL;
    }

    public void setCoverPicURL(String coverPicURL) {
        this.coverPicURL = coverPicURL;
    }

    public List<Mixtape> getMixtapes() {
        return mixtapes;
    }

    public void setMixtapes(List<Mixtape> mixtapes) {
        mixtapes.forEach(mixtape -> mixtape.setPlaylistId(this.id));
        this.mixtapes = mixtapes;
    }

    public void addMixtape(Mixtape mixtape) {
        mixtape.setPlaylistId(this.id);
        this.mixtapes.add(mixtape);
    }

    @JsonProperty
    public long totalDurationMS() {
        return mixtapes.stream()
                .mapToLong(Mixtape::getDurationMS)
                .sum();
    }

    @JsonProperty
    public int songCount() {
        return mixtapes.stream()
                .flatMap(mixtape -> mixtape.getSongIDs().stream())
                .collect(Collectors.toSet())
                .size();
    }
}

