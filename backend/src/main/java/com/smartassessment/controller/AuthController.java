package com.smartassessment.controller;

import com.smartassessment.dto.auth.JwtAuthResponse;
import com.smartassessment.dto.auth.LoginRequest;
import com.smartassessment.dto.auth.RegisterRequest;
import com.smartassessment.dto.auth.UserResponseDto;
import com.smartassessment.dto.common.ApiResponse;
import com.smartassessment.security.CurrentUser;
import com.smartassessment.security.UserPrincipal;
import com.smartassessment.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtAuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "User authenticated successfully"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        UserResponseDto response = authService.register(registerRequest);
        return new ResponseEntity<>(ApiResponse.success(response, "User registered successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        UserResponseDto response = authService.getCurrentUser(currentUser);
        return ResponseEntity.ok(ApiResponse.success(response, "Fetched current user details"));
    }
}
