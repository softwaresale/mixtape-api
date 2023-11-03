package com.mixtape.mixtapeapi.notification;

import com.onesignal.client.ApiException;

public interface PushNotificationService {
    void sendPushForNotification(Notification notification);
}
