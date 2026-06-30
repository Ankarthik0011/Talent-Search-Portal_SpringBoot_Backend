package com.smvml.talentsearch.service;

public interface EmailService {

    void sendOtpEmail(String toEmail, String otp, int expiryMinutes);
}