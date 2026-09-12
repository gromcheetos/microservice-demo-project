package com.example.dto;

import lombok.Data;

@Data
public class RegisterPlatformUserRequest {
    private String name;
    private String email;
    private String password;
}