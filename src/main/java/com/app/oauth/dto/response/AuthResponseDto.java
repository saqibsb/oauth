package com.app.oauth.dto.response;

public class AuthResponseDto {
    private String message;
    private Long userId;
    private String username;
    private String token;

    public AuthResponseDto(String message, Long userId, String username, String token) {
        this.message = message;
        this.userId = userId;
        this.username = username;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}