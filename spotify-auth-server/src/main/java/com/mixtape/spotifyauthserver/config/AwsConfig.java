package com.mixtape.spotifyauthserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

@Configuration
public class AwsConfig {

    @Bean
    public SecretsManagerClient secretsManagerClient(AwsCredentialsProvider awsCredentialsProvider) {
        return SecretsManagerClient.builder()
                .credentialsProvider(awsCredentialsProvider)
                .region(Region.US_EAST_2)
                .build();
    }

    @Profile("prod")
    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        return InstanceProfileCredentialsProvider.builder()
                .build();
    }

    @Profile("!prod")
    @Bean
    public AwsCredentialsProvider localAwsCredentialsProvider() {
        return EnvironmentVariableCredentialsProvider.create();
    }
}
