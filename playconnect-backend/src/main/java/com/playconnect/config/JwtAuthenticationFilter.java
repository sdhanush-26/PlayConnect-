package com.playconnect.config;

import com.playconnect.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Runs once per incoming request, before it reaches any controller.
 * Looks for "Authorization: Bearer <token>", and if present and valid,
 * tells Spring Security who the request is coming from — storing that
 * in SecurityContextHolder for the rest of the request's lifetime.
 *
 * Right now (Day 41) this has no visible effect, since SecurityConfig's
 * permitAll() (Day 38) means no endpoint actually requires being
 * authenticated. Day 43 is when specific endpoints start checking
 * SecurityContextHolder and actually rejecting unauthenticated requests.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // strip "Bearer " prefix

            if (jwtUtil.isTokenValid(token)) {
                String email = jwtUtil.extractEmail(token);
                Long userId = jwtUtil.extractUserId(token);

                // No roles/authorities attached yet — that arrives Day 42
                // when PLAYER/ADMIN roles exist. An empty authorities list
                // just means "we know who this is" without granting any
                // specific permission.
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
                authToken.setDetails(userId);

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            // Invalid/expired token: silently continue unauthenticated
            // rather than rejecting outright here — permitAll() endpoints
            // should still work even with a bad token attached. Day 43's
            // protected endpoints are what actually enforce rejection.
        }

        filterChain.doFilter(request, response);
    }
}
