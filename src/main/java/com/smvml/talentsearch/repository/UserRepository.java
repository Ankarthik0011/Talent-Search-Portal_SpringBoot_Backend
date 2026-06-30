package com.smvml.talentsearch.repository;

import com.smvml.talentsearch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository
        extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByEmail(String email);

    User findByMobile(String mobile);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}