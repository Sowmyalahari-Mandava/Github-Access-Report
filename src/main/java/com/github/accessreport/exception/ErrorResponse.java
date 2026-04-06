package com.github.accessreport.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * DTO for capturing and parsing error responses.
 *
 * This class is used to map error details returned by external APIs
 * (such as GitHub) or internal application exceptions into a structured format.
 *
 * The @JsonIgnoreProperties annotation ensures that unknown fields
 * from the API response do not cause parsing errors.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private String path;
    private String timestamp;
}
