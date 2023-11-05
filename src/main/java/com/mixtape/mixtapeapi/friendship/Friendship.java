package com.mixtape.mixtapeapi.friendship;

import com.mixtape.mixtapeapi.BaseEntity;
import com.mixtape.mixtapeapi.profile.Profile;
import jakarta.persistence.*;

@Entity
public class Friendship extends BaseEntity {
    @ManyToOne
    @JoinColumn(name="initiator_id")
    private Profile initiator;

    @ManyToOne
    @JoinColumn(name="target_id")
    private Profile target;

    public Friendship() {
    }

    public Friendship(String id, Profile initiator, Profile target) {
        this.id = id;
        this.initiator = initiator;
        this.target = target;
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
}
