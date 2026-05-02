package com.example.keycloak;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakInitializer {
    @NonNull
    private final Keycloak keycloak;

    @NonNull
    @Qualifier("keycloakMaster")
    private final Keycloak masterKeycloak;


    @EventListener(ApplicationReadyEvent.class)
    public void initializeRealm() {
        log.info("Checking Keycloak realm setup...");

        ensureRealmExists();
        ensureRolesExist();
        ensureClientExists();
        ensureDefaultRole();

        log.info("Keycloak realm setup complete.");
    }


    // ── Realm ─────────────────────────────────────────────────────────────────

    private void ensureRealmExists() {
        boolean exists = masterKeycloak.realms().findAll()
            .stream()
            .anyMatch(r -> r.getRealm().equals(REALM_NAME));

        if (exists) {
            log.info("Realm '{}' already exists — skipping creation.", REALM_NAME);
            return;
        }

        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(REALM_NAME);
        realm.setEnabled(true);
        realm.setDisplayName("Ecommerce Platform");
        // Access token lifespan in seconds — 30 minutes for development
        realm.setAccessTokenLifespan(1800);

        masterKeycloak.realms().create(realm);
        log.info("Realm '{}' created.", REALM_NAME);
    }

    // ── Roles ─────────────────────────────────────────────────────────────────

    private void ensureRolesExist() {
        List<String> requiredRoles = List.of("USER", "ADMIN");

        List<String> existingRoles = masterKeycloak.realm(REALM_NAME).roles()
            .list()
            .stream()
            .map(RoleRepresentation::getName)
            .toList();

        for (String roleName : requiredRoles) {
            if (existingRoles.contains(roleName)) {
                log.info("Role '{}' already exists — skipping.", roleName);
                continue;
            }
            RoleRepresentation role = new RoleRepresentation();
            role.setName(roleName);
            role.setDescription("Platform role: " + roleName);
            masterKeycloak.realm(REALM_NAME).roles().create(role);
            log.info("Role '{}' created.", roleName);
        }
    }

    // ── Client ────────────────────────────────────────────────────────────────

    private void ensureClientExists() {
        boolean exists = masterKeycloak.realm(REALM_NAME).clients()
            .findAll()
            .stream()
            .anyMatch(c -> CLIENT_ID.equals(c.getClientId()));

        if (exists) {
            log.info("Client '{}' already exists — skipping.", CLIENT_ID);
            return;
        }

        ClientRepresentation client = new ClientRepresentation();
        client.setClientId(CLIENT_ID);
        client.setEnabled(true);
        client.setPublicClient(false);         // confidential client — has a secret
        client.setSecret(CLIENT_SECRET);        // uses the secret from application.yml
        client.setDirectAccessGrantsEnabled(true);  // allows password grant (for testing)
        client.setServiceAccountsEnabled(true);     // allows client_credentials grant
        client.setStandardFlowEnabled(true);

        // Allowed redirect URIs — * is fine for development, lock this down in production
        client.setRedirectUris(List.of("*"));
        client.setWebOrigins(List.of("*"));

        masterKeycloak.realm(REALM_NAME).clients().create(client);
        log.info("Client '{}' created.", CLIENT_ID);
    }

    // ── Default role ──────────────────────────────────────────────────────────

    private void ensureDefaultRole() {
        // Fetch the USER role representation
        RoleRepresentation userRole;
        try {
            userRole = masterKeycloak.realm(REALM_NAME).roles()
                .get("USER")
                .toRepresentation();
        } catch (Exception e) {
            log.warn("USER role not found — cannot set as default.");
            return;
        }

        // Check if USER is already in the realm's default roles
        List<String> currentDefaults = masterKeycloak.realm(REALM_NAME)
            .toRepresentation()
            .getDefaultRoles();

        if (currentDefaults != null && currentDefaults.contains("USER")) {
            log.info("USER is already a default role — skipping.");
            return;
        }

        // Add USER to default roles
        RealmRepresentation realm = masterKeycloak.realm(REALM_NAME).toRepresentation();
        List<String> defaults = realm.getDefaultRoles() != null
            ? new java.util.ArrayList<>(realm.getDefaultRoles())
            : new java.util.ArrayList<>();
        defaults.add("USER");
        realm.setDefaultRoles(defaults);
        masterKeycloak.realm(REALM_NAME).update(realm);
        log.info("USER set as default role for new registrations.");
    }
}