package com.mixtape.mixtapeapi.profile;

import jakarta.persistence.*;

@Entity
public class BlockedProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne
    private Profile blocker;

    @ManyToOne
    private Profile blockee;

    public BlockedProfile() {
        this(null, null, null);
    }

    public BlockedProfile(String id, Profile blocker, Profile blockee) {
        this.id = id;
        this.blocker = blocker;
        this.blockee = blockee;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Profile getBlocker() {
        return blocker;
    }

    public void setBlocker(Profile blocker) {
        this.blocker = blocker;
    }

    public Profile getBlockee() {
        return blockee;
    }

    public void setBlockee(Profile blockee) {
        this.blockee = blockee;
    }
}
