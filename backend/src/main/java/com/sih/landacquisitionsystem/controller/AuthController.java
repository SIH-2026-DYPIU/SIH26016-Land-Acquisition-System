package com.sih.landacquisitionsystem.controller;

import com.sih.landacquisitionsystem.dto.RegisterDTO;
import com.sih.landacquisitionsystem.dto.UserDTO;
import com.sih.landacquisitionsystem.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.email(), request.password());
        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "token", token
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    public record LoginRequest(String email, String password) {}
}
