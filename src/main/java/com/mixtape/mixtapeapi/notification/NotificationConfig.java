package com.mixtape.mixtapeapi.notification;

import com.mixtape.mixtapeapi.notification.onesignal.OneSignalDefaultApiFactory;
import com.mixtape.mixtapeapi.notification.onesignal.OneSignalPushNotificationService;
import com.onesignal.client.ApiClient;
import com.onesignal.client.auth.HttpBearerAuth;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

@Configuration
public class NotificationConfig {

    public static final String NOTIFICATIONS_APP_ID = "notifications-appId";

    private final NotificationConfigProperties notificationConfigProperties;

    public NotificationConfig(NotificationConfigProperties notificationConfigProperties) {
        this.notificationConfigProperties = notificationConfigProperties;
    }

    @Bean
    @Qualifier(NotificationConfig.NOTIFICATIONS_APP_ID)
    public String notificationsAppId() {
        return notificationConfigProperties.getAppId();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ApiClient defaultNotificationApi() {
        // Setting up the client
        ApiClient client = com.onesignal.client.Configuration.getDefaultApiClient();
        HttpBearerAuth appKeyAuth = (HttpBearerAuth) client.getAuthentication("app_key");
        appKeyAuth.setBearerToken(notificationConfigProperties.getAppKey());
        return client;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public OneSignalDefaultApiFactory oneSignalDefaultApiFactory() {
        return new OneSignalDefaultApiFactory(defaultNotificationApi());
    }

    @Profile("prod")
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public PushNotificationService oneSignalNotificationService() {
        return new OneSignalPushNotificationService(oneSignalDefaultApiFactory(), notificationsAppId());
    }

    @Profile("!prod")
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public PushNotificationService nopPushNotificationService() {
        return new NopPushNotificationService();
    }
}
