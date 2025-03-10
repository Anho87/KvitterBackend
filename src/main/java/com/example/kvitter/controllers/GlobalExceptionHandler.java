package com.example.kvitter.controllers;


import com.example.kvitter.exceptions.ExpiredTokenException;
import com.example.kvitter.exceptions.RefreshTokenExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<Map<String, String>> handleExpiredTokenException(ExpiredTokenException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Access token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getReason());
        return ResponseEntity.status(e.getStatusCode()).body(response);
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<Map<String, String>> handleRefreshTokenExpired(RefreshTokenExpiredException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Refresh token expired. Please log in again.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}


