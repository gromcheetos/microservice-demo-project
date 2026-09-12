package com.example.service;

import com.example.dto.PlatformUserResponse;
import com.example.dto.RegisterPlatformUserRequest;
import com.example.keycloak.KeycloakUserService;
import com.example.model.PlatformUser;
import com.example.repository.PlatformUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.Jwt;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PlatformUserRepository userRepository;
    private final KeycloakUserService keycloakUserService;

    @Transactional
    public PlatformUserResponse register(RegisterPlatformUserRequest request) {
    // Step 1: create in Keycloak first.
    // If Keycloak fails, nothing is written to the local DB.
        UUID keycloakId = keycloakUserService.createUser(
                request.getEmail(), request.getName(), request.getPassword());
    // Step 2: persist the domain record.
        PlatformUser user = PlatformUser.builder()
                .id(UUID.randomUUID())
                .keycloakId(keycloakId)
                .name(request.getName())
                .email(request.getEmail())
                .createdAt(LocalDateTime.now())
                .build();
        return toResponse(userRepository.save(user));
    }

    public PlatformUserResponse getPlatformUserProfile(Jwt jwt) {
    // jwt.getSubject() returns the 'sub' claim — the Keycloak user UUID.
        UUID keycloakId = UUID.fromString(jwt.getSubject());
        return userRepository.findByKeycloakId(keycloakId)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException(
                        "No profile found for Keycloak ID: " + keycloakId));
    }

    public List<PlatformUserResponse> getAll() {
        return userRepository.findAllByDeletedAtIsNull()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public PlatformUserResponse getById(UUID id) {
        return userRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    private PlatformUserResponse toResponse(PlatformUser u) {
        return PlatformUserResponse.builder()
                .id(u.getId()).name(u.getName())
                .email(u.getEmail()).createdAt(u.getCreatedAt())
                .build();
    }
}
