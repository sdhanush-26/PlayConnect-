package com.playconnect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Day 38 baseline: adds Spring Security to the project without breaking
 * any existing endpoint. Everything stays open (permitAll) for now —
 * real JWT-based protection gets wired in on Day 43 once login (Day 40)
 * and JWT generation/validation (Day 41) both exist. Building this
 * incrementally rather than locking everything down today, which would
 * break every Postman test built across Days 8-37.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt: industry-standard for password hashing — salts
        // automatically and is deliberately slow to resist brute-force
        // attacks. Used starting Day 39 for registration.
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // not needed for a stateless REST API
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // TEMPORARY — replaced with real rules on Day 43
            );

        return http.build();
    }
}
