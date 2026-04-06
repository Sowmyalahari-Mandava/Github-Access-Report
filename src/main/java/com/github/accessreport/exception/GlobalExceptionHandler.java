package com.github.accessreport.exception;

import com.github.accessreport.constants.ApiConstants;
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

    /**
     * Handles cases where the GitHub organization is not found.
     * @return 404 NOT FOUND response
     */
    @ExceptionHandler(OrganizationNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOrgNotFound(OrganizationNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles GitHub authentication failures (invalid or missing token).
     * @return 401 UNAUTHORIZED response
     */
    @ExceptionHandler(GitHubAuthException.class)
    public ResponseEntity<Map<String, Object>> handleAuthError(GitHubAuthException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    /**
     * Handles GitHub API rate limit exceeded errors.
     * @return 429 TOO MANY REQUESTS response
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleRateLimit(RateLimitExceededException ex) {
        return buildErrorResponse(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
    }

    /**
     * Handles connectivity issues such as no internet or GitHub server downtime.
     * @return 503 SERVICE UNAVAILABLE response
     */
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Map<String, Object>> handleConnectionError(ResourceAccessException ex) {
        return buildErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                ExceptionConstants.GITHUB_CONNECTION_ERROR
        );
    }

    /**
     * Handles all other unexpected exceptions.
     * @return 500 INTERNAL SERVER ERROR response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(Exception ex) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ExceptionConstants.GENERIC_ERROR_PREFIX + ex.getMessage()
        );
    }

    /**
     * Builds a consistent error response body.
     * @return ResponseEntity containing structured error response
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put(ApiConstants.STATUS, status.value());
        errorBody.put(ApiConstants.ERROR, status.getReasonPhrase());
        errorBody.put(ApiConstants.MESSAGE, message);
        errorBody.put(ApiConstants.TIMESTAMP, LocalDateTime.now().toString());

        return ResponseEntity.status(status).body(errorBody);
    }
}