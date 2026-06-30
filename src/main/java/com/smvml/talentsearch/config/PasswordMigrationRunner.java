package com.smvml.talentsearch.config;

import com.smvml.talentsearch.entity.User;
import com.smvml.talentsearch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * One-time migration: your `users` table currently stores plaintext
 * passwords (AuthController used to do password.equals(rawPassword)).
 * On the first startup after this change, this runner scans every user
 * row and re-encodes any password that is not already a BCrypt hash.
 *
 * BCrypt hashes always start with "$2a$", "$2b$", or "$2y$" — that's the
 * detection signal used below. This is safe to leave in permanently:
 * once every row is migrated, it becomes a no-op on every future restart.
 */
@Component
public class PasswordMigrationRunner implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        List<User> users = userRepository.findAll();
        int migrated = 0;

        for (User user : users) {
            String pwd = user.getPassword();

            if (pwd == null || pwd.isBlank()) {
                continue;
            }

            boolean alreadyHashed = pwd.startsWith("$2a$")
                    || pwd.startsWith("$2b$")
                    || pwd.startsWith("$2y$");

            if (!alreadyHashed) {
                user.setPassword(passwordEncoder.encode(pwd));
                userRepository.save(user);
                migrated++;
            }
        }

        if (migrated > 0) {
            System.out.println("[PasswordMigrationRunner] Migrated " + migrated
                    + " plaintext password(s) to BCrypt.");
        }
    }
}