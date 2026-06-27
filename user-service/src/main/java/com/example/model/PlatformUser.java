package com.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(schema = "user_service", name="platform_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformUser {

    @Id
    private UUID id;

    @Column(name="keycloak_id", unique = true)
    private UUID keycloakId;
    private String name;

    @Column(nullable = false, unique = true)
    private String email;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
