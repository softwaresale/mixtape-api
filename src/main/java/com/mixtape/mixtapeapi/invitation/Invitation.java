package com.mixtape.mixtapeapi.invitation;

import com.mixtape.mixtapeapi.profile.Profile;
import jakarta.persistence.*;

@Entity
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name="initiator_id")
    private Profile initiator;

    @ManyToOne
    @JoinColumn(name="target_id")
    private Profile target;
    private InvitationType invitationType;

    private String invitedObjectID;

    public Invitation() {
        this("", null, null, InvitationType.PLAYLIST, "");
    }

    public Invitation(String id, Profile initiator, Profile target, InvitationType invitationType, String invitedObjectID) {
        this.id = id;
        this.initiator = initiator;
        this.target = target;
        this.invitationType = invitationType;
        this.invitedObjectID = invitedObjectID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public InvitationType getInvitationType() {
        return invitationType;
    }

    public void setInvitationType(InvitationType invitationType) {
        this.invitationType = invitationType;
    }

    public String getInvitedObjectID() {
        return invitedObjectID;
    }

    public void setInvitedObjectID(String invitedObjectID) {
        this.invitedObjectID = invitedObjectID;
    }
}