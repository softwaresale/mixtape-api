package com.mixtape.mixtapeapi.notification.onesignal;

import com.mixtape.mixtapeapi.notification.Notification;
import com.mixtape.mixtapeapi.notification.PushNotificationService;
import com.onesignal.client.api.DefaultApi;
import com.onesignal.client.model.CreateNotificationSuccessResponse;
import com.onesignal.client.model.StringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OneSignalPushNotificationService implements PushNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(OneSignalPushNotificationService.class);

    private final OneSignalDefaultApiFactory defaultApiFactory;
    private final String notificationsAppId;

    public OneSignalPushNotificationService(OneSignalDefaultApiFactory defaultApiFactory, String notificationsAppId) {
        this.defaultApiFactory = defaultApiFactory;
        this.notificationsAppId = notificationsAppId;
    }

    @Override
    public void sendPushForNotification(Notification notification) {
        // Log initial message
        logger.info("Pre-Notification send for external-id: {}", notification.getTarget().getId());

        DefaultApi api = defaultApiFactory.createDefaultAPI(notification.getTarget().getId());

        var oneSignalNotification = new com.onesignal.client.model.Notification();

        // Fill out fields
        oneSignalNotification.setAppId(notificationsAppId);
        oneSignalNotification.setIsAndroid(true);
        oneSignalNotification.setIncludeExternalUserIds(List.of(notification.getTarget().getId()));

        // Fill out content
        StringMap contentStringMap = new StringMap();
        contentStringMap.en(notification.getContents());
        oneSignalNotification.setContents(contentStringMap);

        // Send notification
        try {
            logger.info("Dispatching one signal push notification object: {}...", oneSignalNotification);
            CreateNotificationSuccessResponse response = api.createNotification(oneSignalNotification);
            logger.info("Got result: {}", response);
        } catch (Exception e) {
            logger.error("Error while creating and pushing notification", e);
        }
    }
}
