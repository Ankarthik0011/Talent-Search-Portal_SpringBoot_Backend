package com.smvml.talentsearch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * This app does not use Spring Security's session-based login or
 * @PreAuthorize-style method security — auth is handled by your own
 * AuthController/AuthService, with role/email enforcement done manually
 * in CandidateController (see OwnershipValidator). This config exists
 * only to:
 *   1) expose a BCryptPasswordEncoder bean for hashing/verifying passwords
 *   2) disable Spring Security's default login form / CSRF / basic auth,
 *      which would otherwise intercept your API requests and break the
 *      React frontend's existing fetch/axios calls.
 *
 * If you adopt full Spring Security session/JWT auth later, replace the
 * permitAll() rules below with real authorization rules.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable());

        return http.build();
    }
}