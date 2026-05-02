package com.example;

import org.springframework.boot.context.properties.ConfigurationProperties;

public class KeycloakProperties {

    @ConfigurationProperties(prefix = "keycloak")
    public record KeycloakProperties(
            String serverUrl,
            String realm,
            String clientId,
            String clientSecret,
            String adminUsername,
            String adminPassword
    ) {
    }

}
