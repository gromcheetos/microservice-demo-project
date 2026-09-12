package com.example.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PlatformUserResponse {

    private UUID id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}