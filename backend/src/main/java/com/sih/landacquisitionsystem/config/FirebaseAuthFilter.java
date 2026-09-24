package com.sih.landacquisitionsystem.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.sih.landacquisitionsystem.model.User;
import com.sih.landacquisitionsystem.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FirebaseAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        if (token != null && !token.isEmpty()) {
            try {
                // Verify the Firebase ID token
                FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(token);
                String uid = firebaseToken.getUid();
                String email = firebaseToken.getEmail();

                // Look up user in our database by firebaseUid
                Optional<User> userOptional = userRepository.findByFirebaseUid(uid);

                if (userOptional.isPresent()) {
                    User user = userOptional.get();

                    // Check if user is enabled
                    if (!user.isEnabled()) {
                        SecurityContextHolder.clearContext();
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Account is disabled. Please contact administrator.");
                        return;
                    }

                    // Determine authorities based on user role
                    String role = user.getRole();
                    // Handle null or blank role
                    if (role == null || role.isBlank()) {
                        role = "USER"; // Default role
                    }
                    // Ensure role starts with ROLE_ for Spring Security
                    if (!role.startsWith("ROLE_")) {
                        role = "ROLE_" + role;
                    }
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

                    // Create the authentication object with User as principal
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user, null, authorities);

                    // Set the authentication in the security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // If user not found in our database, deny access
                    // Clear security context and send 401 Unauthorized
                    SecurityContextHolder.clearContext();
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("User not authorized. Please contact administrator.");
                    return;
                }

            } catch (Exception e) {
                // If token is invalid, we should not set authentication and let the request fail
                // We can also clear the security context to be safe
                SecurityContextHolder.clearContext();
                // Optionally, log the error
                // logger.error("Firebase token verification failed", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid Firebase token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}