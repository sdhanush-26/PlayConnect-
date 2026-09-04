package com.playconnect.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Day 38 added Spring Security with everything open (permitAll). Day 41
 * adds JwtAuthenticationFilter into the chain so tokens actually get
 * read and validated on every request — but since permitAll() is still
 * active, nothing is rejected yet even without a token. Day 43 replaces
 * permitAll() with real per-endpoint rules once roles exist (Day 42).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityErrorHandlers securityErrorHandlers;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                           SecurityErrorHandlers securityErrorHandlers) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.securityErrorHandlers = securityErrorHandlers;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public — no token required. Registration/login obviously
                // can't require being already logged in. Read-only browsing
                // (search, listings) stays open too, matching how the app
                // has worked since Day 12 — PlayConnect lets people browse
                // before committing to an account, only requiring auth for
                // actions that change data or reveal personal info.
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET,
                        "/api/health", "/api/sports/**", "/api/matches", "/api/matches/**",
                        "/api/players", "/api/players/**", "/api/grounds", "/api/grounds/**")
                .permitAll()

                // Protected — requires a valid token (any role). Covers
                // exactly what the Day 43 plan calls out: Profile, Create
                // Match, Join Match. Chat isn't built yet (Day 52-53), so
                // there's nothing to protect there today.
                .requestMatchers("/api/profile/**").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/matches").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/matches/*/join").authenticated()

                // Admin-only — requires the ADMIN role specifically.
                // Creating/deleting sports and grounds are catalog-level
                // changes that shouldn't be open to every player.
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/sports").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/sports/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/grounds").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/grounds/**").hasRole("ADMIN")

                // Everything else not explicitly listed above also
                // requires authentication — a safe default so newly added
                // endpoints don't accidentally end up wide open.
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(securityErrorHandlers)
                .accessDeniedHandler(securityErrorHandlers)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}