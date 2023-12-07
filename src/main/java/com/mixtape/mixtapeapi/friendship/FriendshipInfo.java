package com.mixtape.mixtapeapi.friendship;

import com.mixtape.mixtapeapi.playlist.Playlist;

import java.util.List;

public class FriendshipInfo {
    private List<Playlist> sharedPlaylists;
    private int numMixtapesFromProfile;
    private int numMixtapesFromFriend;

    public FriendshipInfo(List<Playlist> sharedPlaylists, int numMixtapesFromProfile, int numMixtapesFromFriend) {
        this.sharedPlaylists = sharedPlaylists;
        this.numMixtapesFromProfile = numMixtapesFromProfile;
        this.numMixtapesFromFriend = numMixtapesFromFriend;
    }

    public List<Playlist> getSharedPlaylists() {
        return sharedPlaylists;
    }

    public void setSharedPlaylists(List<Playlist> sharedPlaylists) {
        this.sharedPlaylists = sharedPlaylists;
    }

    public int getNumMixtapesFromProfile() {
        return numMixtapesFromProfile;
    }

    public void setNumMixtapesFromProfile(int numMixtapesFromProfile) {
        this.numMixtapesFromProfile = numMixtapesFromProfile;
    }

    public int getNumMixtapesFromFriend() {
        return numMixtapesFromFriend;
    }

    public void setNumMixtapesFromFriend(int numMixtapesFromFriend) {
        this.numMixtapesFromFriend = numMixtapesFromFriend;
    }
}
