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
@EnableConfigurationProperties(KeycloakProperties.class)  // registers the bean — no @Component needed on KeycloakProperties
public class KeycloakConfig {

    private final KeycloakProperties props;

    public KeycloakConfig(KeycloakProperties props) {
        this.props = props;
    }

    /**
     * Master realm client.
     *
     * Authenticates against the "master" realm using admin credentials.
     * Use this ONLY for realm-level operations:
     *   - Creating or deleting a realm
     *   - Listing all realms
     *
     * Do NOT use this for everyday user/role/client management —
     * use keycloakAdminClient for those.
     */
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

    /**
     * Application realm admin client.
     *
     * Authenticates against your target realm using the client credentials
     * (client_credentials grant). Use this for all day-to-day admin operations:
     *   - Managing users (create, update, delete)
     *   - Assigning roles
     *   - Managing client scopes
     *
     * Requires the client to have "Service Account Roles" enabled in Keycloak
     * with the "realm-management" roles assigned to its service account.
     */
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