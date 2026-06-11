package com.smvml.talentsearch.controller;

import com.smvml.talentsearch.dto.LoginRequest;
import com.smvml.talentsearch.entity.User;
import com.smvml.talentsearch.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public User login(
            @RequestBody LoginRequest request) {

        User user =
                userRepository.findByUsername(
                        request.getUsername());

        if (user == null ||
                !user.getPassword()
                        .equals(request.getPassword())) {

            throw new RuntimeException(
                    "Invalid Credentials");
        }

        return user;
    }
}