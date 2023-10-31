package com.mixtape.mixtapeapi.notification.onesignal;

import com.onesignal.client.ApiClient;
import com.onesignal.client.api.DefaultApi;
import com.onesignal.client.auth.HttpBearerAuth;

public class OneSignalDefaultApiFactory {

    private final ApiClient apiClient;

    public OneSignalDefaultApiFactory(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public DefaultApi createDefaultAPI(String userKey) {
        HttpBearerAuth userKeyAuth = (HttpBearerAuth) apiClient.getAuthentication("user_key");
        userKeyAuth.setBearerToken(userKey);
        return new DefaultApi(apiClient);
    }
}
