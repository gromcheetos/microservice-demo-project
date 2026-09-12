package com.example.service;

import com.example.keycloak.KeycloakUserService;
import com.example.repository.PlatformUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PlatformUserRepository userRepository;
    private final KeycloakUserService keycloakUserService;


}
