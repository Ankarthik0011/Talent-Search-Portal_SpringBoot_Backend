package com.smvml.talentsearch.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpGeneratorUtil {

    private final SecureRandom random = new SecureRandom();

    /**
     * Generates a 6-digit numeric OTP as a zero-padded string,
     * e.g. "042916". Uses SecureRandom rather than Math.random()
     * since this value gates account password resets.
     */
    public String generateOtp() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}