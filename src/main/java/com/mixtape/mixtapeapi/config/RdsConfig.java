package com.mixtape.mixtapeapi.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import javax.sql.DataSource;
import java.util.Optional;

@Profile("prod")
@Configuration
public class RdsConfig {

    private static class RdsDbCredentials {
        private String username;
        private String password;

        public RdsDbCredentials(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("RdsDbCredentials{");
            sb.append("username='").append(username).append('\'');
            sb.append(", password='").append(password).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(RdsConfig.class);

    private final AwsConfigurationProperties awsConfigurationProperties;
    private final ObjectMapper objectMapper;

    public RdsConfig(AwsConfigurationProperties awsConfigurationProperties, ObjectMapper objectMapper) {
        this.awsConfigurationProperties = awsConfigurationProperties;
        this.objectMapper = objectMapper;
    }

    @Bean
    public DataSource dataSourceWithCredentials(SecretsManagerClient secretsManagerClient) {
        DataSourceBuilder<?> builder = DataSourceBuilder.create();
        Optional<RdsDbCredentials> dbCredentials = getCredentials(secretsManagerClient);
        dbCredentials.ifPresent(credentials -> {
            builder.username(credentials.getUsername());
            builder.password(credentials.getPassword());
        });
        builder.url("jdbc:postgresql://mixtape.cosucasfyf1s.us-east-2.rds.amazonaws.com:5432/mixtape");
        return builder.build();
    }

    private Optional<RdsDbCredentials> getCredentials(SecretsManagerClient secretsManagerClient) {
        logger.info("Getting rds secret: {}", awsConfigurationProperties.getDbSecretsId());
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(awsConfigurationProperties.getDbSecretsId())
                .build();
        GetSecretValueResponse response = secretsManagerClient.getSecretValue(request);
        logger.info("Got secret: {}", response.secretString());
        RdsDbCredentials credentials = null;
        try {
            credentials = objectMapper.readValue(response.secretString(), RdsDbCredentials.class);
            logger.info("Got credentials: {}", credentials);
        } catch (JsonProcessingException e) {
            logger.error("Failed to deserialize credentials", e);
        }

        return Optional.ofNullable(credentials);
    }
}
