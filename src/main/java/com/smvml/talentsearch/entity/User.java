package com.smvml.talentsearch.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String mobile;

    // Stored as a BCrypt hash from the moment this change is deployed.
    // See PasswordMigrationRunner for handling of pre-existing
    // plaintext passwords already in the database.
    private String password;

    private String role;

    private LocalDateTime createdAt;
}