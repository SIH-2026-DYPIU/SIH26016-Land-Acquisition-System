package com.sih.landacquisitionsystem.controller;

import com.sih.landacquisitionsystem.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for authentication-related endpoints.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        User user = (User) authentication.getPrincipal();

        Map<String, Object> response = new HashMap<>();
        response.put("uid", user.getFirebaseUid());
        response.put("email", user.getEmail());
        response.put("userId", user.getId());
        response.put("role", user.getRole());
        response.put("authenticated", true);

        return ResponseEntity.ok(response);
    }
}