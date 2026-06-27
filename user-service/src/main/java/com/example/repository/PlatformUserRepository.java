package com.example.repository;

import com.example.model.PlatformUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlatformUserRepository extends JpaRepository<PlatformUser, UUID> {

    Optional<PlatformUser> findByKeycloakId(UUID keycloakId);
    Optional<PlatformUser> findByEmail(String email);
}
