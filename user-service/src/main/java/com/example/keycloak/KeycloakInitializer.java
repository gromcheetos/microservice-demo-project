package com.example.keycloak;


import com.example.KeycloakProperties;
import jakarta.ws.rs.core.Response;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class KeycloakInitializer implements ApplicationRunner {

    private final Keycloak masterClient;
    private final Keycloak adminClient;
    private final KeycloakProperties props;


    public KeycloakInitializer(@Qualifier("keycloakMasterClient") Keycloak masterClient,
                               @Qualifier("keycloakAdminClient") Keycloak adminClient, KeycloakProperties props) {
        this.masterClient = masterClient;
        this.adminClient = adminClient;
        this.props = props;
    }

    // Define the roles your application needs
    private static final List<String> REQUIRED_ROLES = List.of("ROLE_USER", "ROLE_ADMIN");

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting Keycloak initialization...");
        initRealm();
        initClient();
        initRoles();
        log.info("Keycloak initialization complete.");
    }

    // ----------------------------------------------------------------
    // Step 1 — Realm
    // ----------------------------------------------------------------

    private void initRealm() {
        boolean realmExists = masterClient.realms().findAll().stream().anyMatch(r -> r.getRealm().equals(props.getRealm()));

        if (realmExists) {
            log.info("Realm '{}' already exists — skipping creation.", props.getRealm());
            return;
        }

        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(props.getRealm());
        realm.setEnabled(true);
        realm.setDisplayName(props.getRealm());
        // Optional: tune token lifespans, brute-force detection, etc.
        // realm.setAccessTokenLifespan(300);
        // realm.setBruteForceProtected(true);

        masterClient.realms().create(realm);
        log.info("Realm '{}' created successfully.", props.getRealm());
    }

    // ----------------------------------------------------------------
    // Step 2 — Client
    // ----------------------------------------------------------------

    private void initClient() {
        boolean clientExists = masterClient.realm(props.getRealm())  // ← masterClient, not adminClient
                .clients().findByClientId(props.getClientId()).stream().anyMatch(c -> c.getClientId().equals(props.getClientId()));

        if (clientExists) {
            log.info("Client '{}' already exists — skipping creation.", props.getClientId());
            return;
        }

        ClientRepresentation client = new ClientRepresentation();
        client.setClientId(props.getClientId());
        client.setSecret(props.getClientSecret());
        client.setEnabled(true);
        client.setServiceAccountsEnabled(true);
        client.setDirectAccessGrantsEnabled(true);
        client.setPublicClient(false);

        try (Response response = masterClient.realm(props.getRealm()).clients().create(client)) {  // ← masterClient
            if (response.getStatus() == 201) {
                log.info("Client '{}' created successfully.", props.getClientId());
            } else {
                log.error("Failed to create client '{}'. HTTP status: {}", props.getClientId(), response.getStatus());
            }
        }
    }

    // ----------------------------------------------------------------
    // Step 3 — Roles
    // ----------------------------------------------------------------

    private void initRoles() {
        List<String> existingRoles = masterClient.realm(props.getRealm())  // ← masterClient
                .roles().list().stream().map(RoleRepresentation::getName).toList();

        for (String roleName : REQUIRED_ROLES) {
            if (existingRoles.contains(roleName)) {
                log.info("Role '{}' already exists — skipping.", roleName);
                continue;
            }

            RoleRepresentation role = new RoleRepresentation();
            role.setName(roleName);
            role.setDescription("Auto-created role: " + roleName);

            masterClient.realm(props.getRealm()).roles().create(role);  // ← masterClient
            log.info("Role '{}' created successfully.", roleName);
        }
    }

    // ----------------------------------------------------------------
    // (Optional) Step 4 — Default admin user
    // Uncomment if you want a seed user created on first boot.
    // ----------------------------------------------------------------

    @SuppressWarnings("unused")
    private void initDefaultAdminUser(String username, String password) {
        boolean userExists = !adminClient.realm(props.getRealm()).users().search(username).isEmpty();

        if (userExists) {
            log.info("User '{}' already exists — skipping.", username);
            return;
        }

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEnabled(true);
        user.setCredentials(List.of(credential));

        try (Response response = adminClient.realm(props.getRealm()).users().create(user)) {
            if (response.getStatus() == 201) {
                log.info("Default admin user '{}' created.", username);
            } else {
                log.error("Failed to create user '{}'. HTTP status: {}", username, response.getStatus());
            }
        }
    }
}