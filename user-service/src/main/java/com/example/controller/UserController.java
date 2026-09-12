package com.example.controller;

import com.example.dto.PlatformUserResponse;
import com.example.dto.RegisterPlatformUserRequest;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<PlatformUserResponse> register(@RequestBody RegisterPlatformUserRequest req) {
        return ResponseEntity.status(201).body(userService.register(req));
    }

    @GetMapping
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<PlatformUserResponse> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PlatformUserResponse getById(@PathVariable UUID id) {
        return userService.getById(id);
    }

}
