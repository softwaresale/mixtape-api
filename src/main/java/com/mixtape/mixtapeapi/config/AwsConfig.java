package com.mixtape.mixtapeapi.config;

import com.mixtape.mixtapeapi.playlist.PlaylistPicUrlFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {

    private final AwsConfigurationProperties awsConfigurationProperties;

    public AwsConfig(AwsConfigurationProperties awsConfigurationProperties) {
        this.awsConfigurationProperties = awsConfigurationProperties;
    }

    @Bean
    public S3Client s3Client(AwsCredentialsProvider awsCredentialsProvider) {
        return S3Client.builder()
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

    @Bean
    public PlaylistPicUrlFormatter playlistPicUrlFormatter() {
        return new PlaylistPicUrlFormatter(awsConfigurationProperties.getPlaylistPicBucketUrl());
    }
}
