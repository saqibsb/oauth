package com.app.oauth.controller;
import com.app.oauth.config.JwtTokenProvider;
import com.app.oauth.dto.request.LoginRequestDto;
import com.app.oauth.dto.request.RegisterRequestDto;
import com.app.oauth.dto.response.ApiResponse;
import com.app.oauth.dto.response.AuthResponseDto;
import com.app.oauth.dto.response.ProfileResponseDto;
import com.app.oauth.dto.response.RegisterResponseDto;
import com.app.oauth.model.User;
import com.app.oauth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(UserService userService,JwtTokenProvider jwtTokenProvider){
        this.userService = userService;
        this.jwtTokenProvider  = jwtTokenProvider;
    }
    // Register endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid  @RequestBody RegisterRequestDto request) {
            User user = userService.registerUser(request.getUsername(), request.getEmail(), request.getPassword());
            return ResponseEntity.ok(ApiResponse.success("Register successfully", new RegisterResponseDto("Register successfully",user.getId())));
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request) {
        User user = userService.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if(!userService.validatePassword(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtTokenProvider.generateToken(user.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Login successful", new AuthResponseDto("Login successful", user.getId(), user.getUsername(), token)));
    }


    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(ApiResponse.success("Profile data", new ProfileResponseDto("Profile data", username)));
    }
}


