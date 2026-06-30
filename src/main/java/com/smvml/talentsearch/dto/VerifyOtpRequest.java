package com.smvml.talentsearch.dto;

import lombok.Data;

@Data
public class VerifyOtpRequest {

    private String email;

    private String otp;
}