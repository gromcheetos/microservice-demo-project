package com.example.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data // generates getters, setters, equals, hashCode, toString
@NoArgsConstructor // required by Jackson to deserialise JSON into this class
@AllArgsConstructor // convenient constructor for tests
public class UserDTO {

    private UUID id;
    private String name;
    private String email;
}