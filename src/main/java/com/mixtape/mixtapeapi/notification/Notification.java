package com.mixtape.mixtapeapi.notification;

import com.mixtape.mixtapeapi.profile.Profile;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

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

    private String external_id;

    public Notification(String id, Profile target, String contents, NotificationType notificationType, String external_id) {
        this.id = id;
        this.target = target;
        this.contents = contents;
        this.notificationType = notificationType;
        this.external_id = external_id;
    }


    public Notification() {
        this("", null, "", NotificationType.PLAYLIST, "");
    }
}
