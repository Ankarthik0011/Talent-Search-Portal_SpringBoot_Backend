package com.smvml.talentsearch.service;

/**
 * Scaffold for SMS OTP delivery. No implementation is wired in yet —
 * email OTP is the active delivery channel. To add SMS later (e.g. Twilio,
 * MSG91, AWS SNS), implement this interface and register it as a @Service,
 * then call it from AuthServiceImpl alongside (or instead of) EmailService.
 */
public interface SmsService {

    void sendOtpSms(String mobileNumber, String otp);
}