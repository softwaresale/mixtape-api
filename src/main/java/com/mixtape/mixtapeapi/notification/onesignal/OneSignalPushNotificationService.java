package com.mixtape.mixtapeapi.notification.onesignal;

import com.mixtape.mixtapeapi.notification.Notification;
import com.mixtape.mixtapeapi.notification.PushNotificationService;
import com.onesignal.client.ApiException;
import com.onesignal.client.api.DefaultApi;
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
    public void sendPushForNotification(Notification notification) throws ApiException {
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
        api.createNotification(oneSignalNotification);
        logger.info("Dispatching one signal push notification object: {}", oneSignalNotification);
    }
}
