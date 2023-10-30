package com.mixtape.mixtapeapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("aws")
public class AwsConfigurationProperties {
    private String playlistPicBucketUrl;

    public String getPlaylistPicBucketUrl() {
        return playlistPicBucketUrl;
    }

    public void setPlaylistPicBucketUrl(String playlistPicBucketUrl) {
        this.playlistPicBucketUrl = playlistPicBucketUrl;
    }
}
