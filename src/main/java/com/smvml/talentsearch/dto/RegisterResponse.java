package com.smvml.talentsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {

    private Long id;

    private String fullName;

    private String username;

    private String email;

    private String role;

    private String message;
}