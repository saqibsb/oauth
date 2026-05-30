package com.app.oauth.controller;

import com.app.oauth.config.JwtTokenProvider;
import com.app.oauth.dto.request.LoginRequestDto;
import com.app.oauth.dto.request.RefreshTokenRequestDto;
import com.app.oauth.dto.request.RegisterRequestDto;
import com.app.oauth.dto.response.ApiResponse;
import com.app.oauth.dto.response.AuthResponseDto;
import com.app.oauth.dto.response.ProfileResponseDto;
import com.app.oauth.dto.response.RefreshTokenResponseDto;
import com.app.oauth.dto.response.RegisterResponseDto;
import com.app.oauth.model.RefreshToken;
import com.app.oauth.model.User;
import com.app.oauth.service.RefreshTokenService;
import com.app.oauth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthController(UserService userService,
                          JwtTokenProvider jwtTokenProvider,
                          RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponseDto>> register(@Valid @RequestBody RegisterRequestDto request) {
        User user = userService.registerUser(request.getUsername(), request.getEmail(), request.getPassword());
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Register successfully",
                        new RegisterResponseDto("Register successfully", user.getId())
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
        User user = userService.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!userService.validatePassword(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String accessToken = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        AuthResponseDto response = new AuthResponseDto(
                "Login successful",
                user.getId(),
                user.getUsername(),
                accessToken,
                refreshToken.getToken()
        );

        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponseDto>> refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();
        String newAccessToken = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());

        RefreshTokenResponseDto response = new RefreshTokenResponseDto(
                "Token refreshed successfully",
                newAccessToken
        );

        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@Valid @RequestBody RefreshTokenRequestDto request) {
        refreshTokenService.revokeToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", "Token revoked"));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileResponseDto>> getProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(ApiResponse.success("Profile data", new ProfileResponseDto("Profile data", username)));
    }
}