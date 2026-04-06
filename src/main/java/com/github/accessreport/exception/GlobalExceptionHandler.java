package com.github.accessreport.exception;

import com.github.accessreport.constants.ExceptionConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/** This class catches all exceptions thrown anywhere in the app
 * and returns a clean JSON error response instead of a stack trace
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles: organization name not found on GitHub
    @ExceptionHandler(OrganizationNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOrgNotFound(OrganizationNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // Handles: bad or missing GitHub token
    @ExceptionHandler(GitHubAuthException.class)
    public ResponseEntity<Map<String, Object>> handleAuthError(GitHubAuthException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    // Handles: too many requests to GitHub API
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleRateLimit(RateLimitExceededException ex) {
        return buildErrorResponse(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
    }

    // Handles: no internet / GitHub server is down
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Map<String, Object>> handleConnectionError(ResourceAccessException ex) {
        return buildErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                ExceptionConstants.GITHUB_CONNECTION_ERROR
        );
    }

    // Handles: anything else we didn't predict
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(Exception ex) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ExceptionConstants.GENERIC_ERROR_PREFIX + ex.getMessage()
        );
    }

    // Helper method to build a consistent error response body
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("status", status.value());
        errorBody.put("error", status.getReasonPhrase());
        errorBody.put("message", message);
        errorBody.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.status(status).body(errorBody);
    }
}