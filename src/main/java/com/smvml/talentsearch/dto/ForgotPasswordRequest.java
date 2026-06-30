package com.smvml.talentsearch.dto;

import lombok.Data;

@Data
public class ForgotPasswordRequest {

    // Email OR mobile number — resolved to a user account server-side.
    private String identifier;
}