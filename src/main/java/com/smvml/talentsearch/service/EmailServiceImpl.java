package com.smvml.talentsearch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from-name:TalentSearch Portal}")
    private String fromName;

    @Override
    public void sendOtpEmail(String toEmail, String otp, int expiryMinutes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Your TalentSearch Portal verification code");
            helper.setFrom("no-reply@talentsearchportal.com", fromName);
            helper.setText(buildHtmlTemplate(otp, expiryMinutes), true);

            mailSender.send(message);

        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            // Surface as a runtime exception so the controller's global
            // exception handler can turn it into a clean error response
            // instead of the request hanging or returning a raw 500 stack trace.
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage(), e);
        }
    }

    private String buildHtmlTemplate(String otp, int expiryMinutes) {
        return "<!DOCTYPE html>"
                + "<html><body style='margin:0;padding:0;background:#0f172a;font-family:Segoe UI,Arial,sans-serif;'>"
                + "<table width='100%' cellpadding='0' cellspacing='0' style='background:#0f172a;padding:40px 0;'>"
                + "<tr><td align='center'>"
                + "<table width='420' cellpadding='0' cellspacing='0' style='background:#0f172a;border:1px solid #1e293b;border-radius:20px;padding:36px;'>"
                + "<tr><td align='center' style='padding-bottom:24px;'>"
                + "<div style='width:56px;height:56px;border-radius:16px;background:linear-gradient(135deg,#6366f1,#8b5cf6);display:inline-block;line-height:56px;font-size:24px;color:white;'>&#9889;</div>"
                + "</td></tr>"
                + "<tr><td align='center' style='color:#f1f5f9;font-size:20px;font-weight:800;padding-bottom:6px;'>TalentSearch Portal</td></tr>"
                + "<tr><td align='center' style='color:#64748b;font-size:13px;padding-bottom:28px;'>Verification Code</td></tr>"
                + "<tr><td align='center' style='color:#94a3b8;font-size:14px;padding-bottom:20px;'>Use the code below to continue. This code is valid for "
                + expiryMinutes + " minutes.</td></tr>"
                + "<tr><td align='center' style='padding-bottom:28px;'>"
                + "<div style='display:inline-block;background:rgba(99,102,241,0.12);border:1px solid rgba(99,102,241,0.3);border-radius:12px;padding:16px 32px;'>"
                + "<span style='color:#a5b4fc;font-size:32px;font-weight:800;letter-spacing:8px;'>" + otp + "</span>"
                + "</div></td></tr>"
                + "<tr><td align='center' style='color:#475569;font-size:12px;padding-bottom:8px;'>"
                + "If you didn't request this code, you can safely ignore this email.</td></tr>"
                + "<tr><td align='center' style='color:#334155;font-size:11px;padding-top:20px;border-top:1px solid #1e293b;'>"
                + "&copy; 2026 TalentSearch Portal &middot; All rights reserved</td></tr>"
                + "</table>"
                + "</td></tr></table>"
                + "</body></html>";
    }
}