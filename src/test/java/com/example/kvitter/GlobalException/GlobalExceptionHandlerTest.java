package com.example.kvitter.GlobalException;

import com.example.kvitter.controllers.GlobalExceptionHandler;
import com.example.kvitter.exceptions.ExpiredTokenException;
import com.example.kvitter.exceptions.RefreshTokenExpiredException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleExpiredTokenException() {
        ExpiredTokenException exception = new ExpiredTokenException("Token expired");

        ResponseEntity<Map<String, String>> response = handler.handleExpiredTokenException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Access token expired");
    }

    @Test
    void testHandleRefreshTokenExpiredException() {
        RefreshTokenExpiredException exception = new RefreshTokenExpiredException("Refresh expired");

        ResponseEntity<Map<String, String>> response = handler.handleRefreshTokenExpired(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Refresh token expired. Please log in again.");
    }

    @Test
    void testHandleResponseStatusException() {
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request error");

        ResponseEntity<Map<String, String>> response = handler.handleResponseStatusException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Bad request error");
    }
}

