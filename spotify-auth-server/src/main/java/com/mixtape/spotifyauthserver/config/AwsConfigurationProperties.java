package com.mixtape.spotifyauthserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("mixtape.aws")
public class AwsConfigurationProperties {

    private String rdsSecretId;

    public String getRdsSecretId() {
        return rdsSecretId;
    }

    public void setRdsSecretId(String rdsSecretId) {
        this.rdsSecretId = rdsSecretId;
    }
}
