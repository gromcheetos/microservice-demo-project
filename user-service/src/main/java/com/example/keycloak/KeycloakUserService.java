package com.example.keycloak;

import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class KeycloakUserService {

    private final Keycloak adminClient;
    private final KeycloakProperties props;

    public KeycloakUserService(@Qualifier("keycloakAdminClient") Keycloak adminClient, KeycloakProperties props) {
        this.adminClient = adminClient;
        this.props = props;
    }

    public UUID createUser(String email, String name, String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setEmail(email);
        user.setUsername(email); // use email as username
        user.setEmailVerified(true);
        String[] parts = name.split(" ", 2);
        user.setFirstName(parts[0]);
        user.setLastName(parts.length > 1 ? parts[1] : "");
        user.setCredentials(List.of(credential));
        try (Response response = adminClient.realm(props.getRealm()).users().create(user)) {
            if (response.getStatus() == 409) {
                throw new IllegalStateException("Email already registered: " + email);
            }
            if (response.getStatus() != 201) {
                throw new RuntimeException(
                        "Keycloak user creation failed. Status: " + response.getStatus());
            }
// Keycloak returns the new user URL in the Location header.
// Extract the UUID from the end of that URL.
            String location = response.getHeaderString("Location");
            UUID keycloakId = UUID.fromString(
                    location.substring(location.lastIndexOf("/") + 1));
            log.info("Keycloak user created: {}", keycloakId);
            return keycloakId;
        }
    }

//    public void disableUser(UUID keycloakId) {
//        var resource = adminClient.realm(props.getRealm())
//                .users().get(keycloakId.toString());
//        UserRepresentation user = resource.toRepresentation();
//        user.setEnabled(false);
//        resource.update(user);
//        log.info("Keycloak user disabled: {}", keycloakId);
//    }
}
