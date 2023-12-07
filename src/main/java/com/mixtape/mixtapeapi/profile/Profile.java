package com.mixtape.mixtapeapi.profile;

import com.mixtape.mixtapeapi.profile.projections.SpotifyIdOnly;
import jakarta.persistence.*;

@Entity
public class Profile implements SpotifyIdOnly {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String spotifyUID;
    private String displayName;
    private String profilePicURL;
    private Boolean onboarded;

    public Profile() {
        this(null, null, null, null, false);
    }

    public Profile(String id, String spotifyUID, String displayName, String profilePicURL) {
        this(id, spotifyUID, displayName, profilePicURL, false);
    }

    public Profile(String id, String spotifyUID, String displayName, String profilePicURL, boolean onboarded) {
        this.id = id;
        this.spotifyUID = spotifyUID;
        this.displayName = displayName;
        this.profilePicURL = profilePicURL;
        this.onboarded = onboarded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
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

    public Boolean getOnboarded() {
        return onboarded;
    }

    public void setOnboarded(Boolean onboarded) {
        this.onboarded = onboarded;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Profile{");
        sb.append("id='").append(id).append('\'');
        sb.append(", spotifyUID='").append(spotifyUID).append('\'');
        sb.append(", displayName='").append(displayName).append('\'');
        sb.append(", profilePicURL='").append(profilePicURL).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
