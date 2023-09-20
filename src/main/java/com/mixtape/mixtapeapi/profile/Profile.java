package com.mixtape.mixtapeapi.profile;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String spotifyUID;
    private String displayName;
    private String profilePicURL;

    public Profile() {
    }

    public Profile(String id, String spotifyUID, String displayName, String profilePicURL) {
        this.id = id;
        this.spotifyUID = spotifyUID;
        this.displayName = displayName;
        this.profilePicURL = profilePicURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpotifyUID() {
        return spotifyUID;
    }

    public void setSpotifyUID(String spotifyUID) {
        this.spotifyUID = spotifyUID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }
}
