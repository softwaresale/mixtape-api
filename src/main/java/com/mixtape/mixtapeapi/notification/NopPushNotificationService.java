package com.mixtape.mixtapeapi.notification;

import com.onesignal.client.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NopPushNotificationService implements PushNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NopPushNotificationService.class);

    @Override
    public void sendPushForNotification(Notification notification) throws ApiException {
        logger.info("No push notification is sent for {}", notification);
    }
}
