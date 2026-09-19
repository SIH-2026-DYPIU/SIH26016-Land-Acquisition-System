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
                User user = userOptional.orElseGet(() -> {
                    // If user not found, we can create a new user entry with default role?
                    // For security, we might want to reject the request. However, for MVP we'll create a pending user.
                    // We'll create a user with minimal info and role ROLE_USER.
                    User newUser = User.builder()
                            .firebaseUid(uid)
                            .email(email != null ? email : "")
                            .name(uid) // Use UID as name placeholder
                            .role("ROLE_USER")
                            .enabled(true)
                            .build();
                    // We cannot save the user here because we are in a filter and might not have transaction support.
                    // Instead, we'll return a user object that is not saved, but we need to persist it later.
                    // For simplicity, we'll just use a temporary user with role ROLE_USER and not persist.
                    // In a real app, we would have a registration flow.
                    return newUser;
                });

                // Determine authorities based on user role
                String role = user.getRole();
                // Ensure role starts with ROLE_ for Spring Security
                if (!role.startsWith("ROLE_")) {
                    role = "ROLE_" + role;
                }
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

                // Create the authentication object
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        uid, null, authorities);

                // Optionally, set additional details like email
                authentication.setDetails(email);

                // Set the authentication in the security context
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // If token is invalid, we should not set authentication and let the request fail
                // We can also clear the security context to be safe
                SecurityContextHolder.clearContext();
                // Optionally, log the error
                // logger.error("Firebase token verification failed", e);
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