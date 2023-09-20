package com.mixtape.mixtapeapi.friendship;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String initiatorID;
    private String targetID;

    public Friendship() {

    }

    public Friendship(String id, String initiatorID, String targetID) {
        this.id = id;
        this.initiatorID = initiatorID;
        this.targetID = targetID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInitiatorID() {
        return initiatorID;
    }

    public void setInitiatorID(String initiatorID) {
        this.initiatorID = initiatorID;
    }

    public String getTargetID() {
        return targetID;
    }

    public void setTargetID(String targetID) {
        this.targetID = targetID;
    }
}
