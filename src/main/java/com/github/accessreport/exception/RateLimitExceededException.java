package com.github.accessreport.exception;

import com.github.accessreport.constants.ExceptionConstants;

/**
 * Custom exception thrown when GitHub API rate limits are exceeded.
 *
 * This typically occurs when:
 * 1. Too many API requests are made in a short period
 * 2. GitHub returns HTTP 403 or 429 status codes
 *
 * This exception extends RuntimeException for unchecked handling.
 */
public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException() {
        super(ExceptionConstants.RATE_LIMIT_EXCEEDED);
    }
}
