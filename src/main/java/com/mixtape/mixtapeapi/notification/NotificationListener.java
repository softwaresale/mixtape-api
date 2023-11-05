package com.mixtape.mixtapeapi.notification;

import jakarta.persistence.PostPersist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationListener.class);
    private final PushNotificationService pushNotificationService;

    public NotificationListener(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    public NotificationListener() {
        this(new NopPushNotificationService());
    }

    @PostPersist
    public void postPersist(Notification notification) {
        // Create api
        logger.info("New notification created: {}", notification);
        pushNotificationService.sendPushForNotification(notification);
    }
}
