package com.app.oauth.exception;

import com.app.oauth.dto.response.ApiResponse;
import com.app.oauth.dto.response.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponseDto>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.toList());

        ErrorResponseDto error = new ErrorResponseDto();
        error.setError("VALIDATION_ERROR");
        error.setMessage("Invalid request payload");
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setTimestamp(LocalDateTime.now());
        error.setDetails(details);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure("Validation failed", error));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<ErrorResponseDto>> handleRuntimeException(RuntimeException ex) {
        ErrorResponseDto error = new ErrorResponseDto();
        error.setError("BAD_REQUEST");
        error.setMessage(ex.getMessage() != null ? ex.getMessage() : "Request failed");
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setTimestamp(LocalDateTime.now());
        error.setDetails(null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure("Request failed", error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponseDto>> handleGenericException(Exception ex) {
        ErrorResponseDto error = new ErrorResponseDto();
        error.setError("INTERNAL_SERVER_ERROR");
        error.setMessage("Something went wrong");
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setTimestamp(LocalDateTime.now());
        error.setDetails(null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("Unexpected error", error));
    }

    private String formatFieldError(FieldError fieldError) {
        String defaultMessage = fieldError.getDefaultMessage() != null
                ? fieldError.getDefaultMessage()
                : "Invalid value";
        return fieldError.getField() + ": " + defaultMessage;
    }
}

//package com.app.oauth.exception;
//import com.app.oauth.dto.response.ErrorResponseDto;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<ErrorResponseDto> handleRuntimeException(RuntimeException ex) {
//        ErrorResponseDto error = new ErrorResponseDto(
//                "BAD_REQUEST",
//                ex.getMessage(),
//                HttpStatus.BAD_REQUEST.value(),
//                LocalDateTime.now(),
//                null
//        );
//
//        return ResponseEntity.badRequest().body(error);
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException ex) {
//        List<String> details = ex.getBindingResult()
//                .getAllErrors()
//                .stream()
//                .map(err -> {
//                    if (err instanceof FieldError fieldError) {
//                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
//                    }
//                    return err.getDefaultMessage();
//                })
//                .collect(Collectors.toList());
//
//        ErrorResponseDto error = new ErrorResponseDto(
//                "VALIDATION_ERROR",
//                "Invalid request payload",
//                HttpStatus.BAD_REQUEST.value(),
//                LocalDateTime.now(),
//                details
//        );
//
//        return ResponseEntity.badRequest().body(error);
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
//        ErrorResponseDto error = new ErrorResponseDto(
//                "INTERNAL_SERVER_ERROR",
//                "Something went wrong",
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                LocalDateTime.now(),
//                null
//        );
//
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//    }
//}