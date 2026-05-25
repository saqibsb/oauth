package com.app.oauth.dto.response;
import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponseDto {
    private String error;
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private List<String> details;

    public ErrorResponseDto() {
    }

    public ErrorResponseDto(String error, String message, int status, LocalDateTime timestamp, List<String> details) {
        this.error = error;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.details = details;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}