package com.example.keycloak;

import com.example.KeycloakProperties;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KeycloakConfig {
    private final KeycloakProperties props;

    @Bean(name = "keycloakMasterClient", destroyMethod = "close")
    public Keycloak keycloakMasterClient() {
        return KeycloakBuilder.builder()
                .serverUrl(props.getServerUrl())
                .realm(props.getMasterRealm())
                .clientId(props.getMasterClientId())
                .grantType(OAuth2Constants.PASSWORD)
                .username(props.getMasterUsername())
                .password(props.getMasterPassword())
                .build();
    }

    @Bean(name = "keycloakAdminClient", destroyMethod = "close")
    public Keycloak keycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(props.getServerUrl())
                .realm(props.getRealm())
                .clientId(props.getClientId())
                .clientSecret(props.getClientSecret())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}