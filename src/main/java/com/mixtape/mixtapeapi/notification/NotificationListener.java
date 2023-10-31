package com.mixtape.mixtapeapi.notification;

import com.mixtape.mixtapeapi.config.NotificationConfig;
import com.mixtape.mixtapeapi.config.NotificationConfigProperties;
import com.onesignal.client.ApiClient;
import com.onesignal.client.ApiException;
import com.onesignal.client.api.DefaultApi;
import com.onesignal.client.model.StringMap;
import jakarta.persistence.PostPersist;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationListener {

    private ApiClient apiClient;
    private NotificationConfigProperties notificationConfigProperties;

    public NotificationListener(ApiClient apiClient, NotificationConfigProperties notificationConfigProperties) {
        this.apiClient = apiClient;
        this.notificationConfigProperties = notificationConfigProperties;
    }

    public NotificationListener() {

    }

    @PostPersist
    public void postPersist(Notification notification) throws ApiException {
        // Create api
        DefaultApi api = NotificationConfig.setUserKeyAndCreateApi(apiClient, notification.getTarget().getId());

        // Create notification
        var oneSignalNotification = new com.onesignal.client.model.Notification();

        // Fill out fields
        oneSignalNotification.setAppId(notificationConfigProperties.getAppId());
        oneSignalNotification.setIsAndroid(true);
        oneSignalNotification.setIncludeExternalUserIds(List.of(notification.getTarget().getId()));

        // Fill out content
        StringMap contentStringMap = new StringMap();
        contentStringMap.en(notification.getContents());
        oneSignalNotification.setContents(contentStringMap);

        // Send notification
        api.createNotification(oneSignalNotification);
    }
}
