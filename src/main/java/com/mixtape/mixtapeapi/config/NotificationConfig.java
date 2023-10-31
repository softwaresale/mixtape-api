package com.mixtape.mixtapeapi.config;

import com.onesignal.client.ApiClient;
import com.onesignal.client.api.DefaultApi;
import com.onesignal.client.auth.HttpBearerAuth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {

    private final NotificationConfigProperties notificationConfigProperties;

    public NotificationConfig(NotificationConfigProperties notificationConfigProperties) {
        this.notificationConfigProperties = notificationConfigProperties;
    }

    @Bean
    public ApiClient createDefaultNotificationApi() {
        // Setting up the client
        ApiClient client = com.onesignal.client.Configuration.getDefaultApiClient();
        HttpBearerAuth appKeyAuth = (HttpBearerAuth) client.getAuthentication("app_key");
        appKeyAuth.setBearerToken(notificationConfigProperties.getAppKey());
        return client;
    }

    public static DefaultApi setUserKeyAndCreateApi(ApiClient client, String userKey) {
        HttpBearerAuth userKeyAuth = (HttpBearerAuth) client.getAuthentication("user_key");
        userKeyAuth.setBearerToken(userKey);
        return new DefaultApi(client);
    }
}
