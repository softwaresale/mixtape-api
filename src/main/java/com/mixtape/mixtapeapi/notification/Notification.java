package com.mixtape.mixtapeapi.notification;

import com.mixtape.mixtapeapi.BaseEntity;
import com.mixtape.mixtapeapi.profile.Profile;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@EntityListeners(NotificationListener.class)
@Entity
public class Notification extends BaseEntity {
    @ManyToOne
    @JoinColumn(name="initiator_id")
    private Profile initiator;

    @ManyToOne
    @JoinColumn(name="target_id")
    private Profile target;

    private String contents;

    private NotificationType notificationType;

    private String externalId;

    public Notification(String id, Profile initiator, Profile target, String contents, NotificationType notificationType, String externalId) {
        this.id = id;
        this.initiator = initiator;
        this.target = target;
        this.contents = contents;
        this.notificationType = notificationType;
        this.externalId = externalId;
    }

    public Notification() {
        this("", null, null, "", NotificationType.PLAYLIST, "");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Notification{");
        sb.append("id='").append(id).append('\'');
        sb.append(", target=").append(target);
        sb.append(", contents='").append(contents).append('\'');
        sb.append(", notificationType=").append(notificationType);
        sb.append(", externalId='").append(externalId).append('\'');
        sb.append('}');
        return sb.toString();
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

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}
