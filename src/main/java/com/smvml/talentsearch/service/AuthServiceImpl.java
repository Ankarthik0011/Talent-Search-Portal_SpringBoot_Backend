package com.smvml.talentsearch.service;

import com.smvml.talentsearch.dto.*;
import com.smvml.talentsearch.entity.PasswordResetToken;
import com.smvml.talentsearch.entity.User;
import com.smvml.talentsearch.repository.PasswordResetTokenRepository;
import com.smvml.talentsearch.repository.UserRepository;
import com.smvml.talentsearch.util.OtpGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpGeneratorUtil otpGeneratorUtil;

    @Value("${app.otp.expiry-minutes:5}")
    private int otpExpiryMinutes;

    // Simple in-memory brute-force guard: tracks failed login attempts
    // per username. Resets on successful login. For multi-instance
    // deployments, replace with a shared store (Redis) instead.
    private final java.util.Map<String, Integer> failedAttempts = new java.util.concurrent.ConcurrentHashMap<>();
    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Override
    public LoginResponse login(LoginRequest request) {

        String username = request.getUsername();
        int attempts = failedAttempts.getOrDefault(username, 0);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Too many failed login attempts. Please try again later.");
        }

        User user = userRepository.findByUsername(username);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            failedAttempts.put(username, attempts + 1);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Credentials");
        }

        failedAttempts.remove(username);

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);

        return new RegisterResponse(
                saved.getId(),
                saved.getFullName(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getRole(),
                "Account created successfully"
        );
    }

    @Override
    public void sendOtp(ForgotPasswordRequest request) {

        String identifier = request.getIdentifier();
        if (identifier == null || identifier.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or mobile number is required");
        }

        User user = identifier.contains("@")
                ? userRepository.findByEmail(identifier)
                : userRepository.findByMobile(identifier);

        if (user == null) {
            // Avoid confirming/denying account existence in the response
            // message — but we still must stop here since there's no
            // email to send to.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No account found for the provided email or mobile number");
        }

        String otp = otpGeneratorUtil.generateOtp();

        PasswordResetToken token = new PasswordResetToken();
        token.setEmail(user.getEmail());
        token.setOtp(otp);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiryTime(LocalDateTime.now().plusMinutes(otpExpiryMinutes));
        token.setUsed(false);
        tokenRepository.save(token);

        emailService.sendOtpEmail(user.getEmail(), otp, otpExpiryMinutes);
    }

    @Override
    public void verifyOtp(VerifyOtpRequest request) {
        getValidUnusedToken(request.getEmail(), request.getOtp());
        // Verification only checks validity here; it does not mark the
        // token used yet — that happens at the final resetPassword step,
        // so the same OTP can be safely re-validated by VerifyOtp.jsx
        // before the user actually submits a new password.
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {

        PasswordResetToken token = getValidUnusedToken(request.getEmail(), request.getOtp());

        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);
    }

    private PasswordResetToken getValidUnusedToken(String email, String otp) {

        PasswordResetToken token = tokenRepository
                .findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "No active OTP request found. Please request a new code."));

        if (token.isUsed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This OTP has already been used");
        }

        if (LocalDateTime.now().isAfter(token.getExpiryTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This OTP has expired. Please request a new code.");
        }

        if (!token.getOtp().equals(otp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect OTP");
        }

        return token;
    }
}