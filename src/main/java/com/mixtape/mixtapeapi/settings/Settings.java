package com.mixtape.mixtapeapi.settings;

import com.mixtape.mixtapeapi.profile.Profile;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import java.util.Collections;
import java.util.List;

@Entity
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "profileId")
    private Profile profile;

    private boolean hasProfilePermission;

    @OneToMany
    @JoinColumn(name = "friendIdsWithPermission")
    private List<Profile> friendsWithPermission;

    public Settings() {
        this("", null, false, Collections.emptyList());
    }

    public Settings(String id, Profile profile, boolean hasProfilePermission, List<Profile> friendsWithPermission) {
        this.id = id;
        this.profile = profile;
        this.hasProfilePermission = hasProfilePermission;
        this.friendsWithPermission = friendsWithPermission;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public boolean isHasProfilePermission() {
        return hasProfilePermission;
    }

    public void setHasProfilePermission(boolean hasProfilePermission) {
        this.hasProfilePermission = hasProfilePermission;
    }

    public List<Profile> getFriendsWithPermission() {
        return friendsWithPermission;
    }

    public void setFriendsWithPermission(List<Profile> friendsWithPermission) {
        this.friendsWithPermission = friendsWithPermission;
    }
}
