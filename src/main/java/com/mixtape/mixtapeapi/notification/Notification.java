package com.mixtape.mixtapeapi.notification;

import com.mixtape.mixtapeapi.profile.Profile;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@EntityListeners(NotificationListener.class)
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name="target_id")
    private Profile target;

    private String contents;

    private NotificationType notificationType;

    private String externalId;

    public Notification(String id, Profile target, String contents, NotificationType notificationType, String externalId) {
        this.id = id;
        this.target = target;
        this.contents = contents;
        this.notificationType = notificationType;
        this.externalId = externalId;
    }


    public Notification() {
        this("", null, "", NotificationType.PLAYLIST, "");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setExternalId(String external_id) {
        this.externalId = external_id;
    }
}
