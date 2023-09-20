package com.mixtape.mixtapeapi.invitation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String initiatorID;
    private String targetID;
    private String invitationType;

    public Invitation() {
    }

    public Invitation(String id, String initiatorID, String targetID, String invitationType) {
        this.id = id;
        this.initiatorID = initiatorID;
        this.targetID = targetID;
        this.invitationType = invitationType;
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

    public String getInvitationType() {
        return invitationType;
    }

    public void setInvitationType(String invitationType) {
        this.invitationType = invitationType;
    }
}