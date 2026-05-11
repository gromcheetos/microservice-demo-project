package com.example;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

    private String serverUrl;

    // -------------------------
    // Master realm credentials
    // Used only for realm-level admin operations (create/delete realm)
    // -------------------------

    /** Always "master" unless you've renamed it */
    private String masterRealm = "master";

    /** Typically "admin-cli" */
    private String masterClientId = "admin-cli";

    private String masterUsername;
    private String masterPassword;

    // -------------------------
    // Target realm & client
    // Used for application-level admin operations
    // -------------------------

    /** The realm your application will operate in */
    private String realm;

    /** The client ID registered in Keycloak for this app */
    private String clientId;

    /** The client secret (for confidential clients) */
    private String clientSecret;
}