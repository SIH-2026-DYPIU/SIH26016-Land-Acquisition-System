package com.sih.landacquisitionsystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Test endpoint to verify Firebase authentication.
 * Returns the authenticated Firebase user's UID and email.
 */
@RestController
@RequestMapping("/api/test")
public class TestAuthController {

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(null);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("uid", authentication.getPrincipal());
        // If we set details as email in the filter, we can retrieve it here
        Object details = authentication.getDetails();
        if (details != null) {
            response.put("email", details.toString());
        }

        return ResponseEntity.ok(response);
    }
}