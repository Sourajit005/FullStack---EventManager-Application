package com.example.event_manager.controller;

import com.example.event_manager.dto.AuthResponse;
import com.example.event_manager.dto.LoginRequest;
import com.example.event_manager.dto.RegisterRequest;
import com.example.event_manager.dto.MessageResponse;
import com.example.event_manager.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // 1. Try to register the user
            AuthResponse authResponse = authService.register(request);
            // 2. If it succeeds, return 200 OK
            return ResponseEntity.ok(authResponse);

        } catch (RuntimeException e) {
            // 3. If it fails (e.g., duplicate user), catch the error
            // 4. Return a 400 Bad Request with the specific error message
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        // Spring Security will handle bad credentials exceptions
        return ResponseEntity.ok(authService.login(request));
    }
}