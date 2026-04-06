package com.github.accessreport.exception;

import com.github.accessreport.constants.ExceptionConstants;

/**
 * Custom exception thrown when GitHub authentication fails.
 *
 * This typically occurs when:
 * 1. The GitHub token is missing
 * 2. The token is invalid or expired
 * 3. Unauthorized (HTTP 401) response is received from GitHub API
 *
 * This exception extends RuntimeException to allow unchecked exception handling.
 */
public class GitHubAuthException extends RuntimeException {

    public GitHubAuthException() {
        super(ExceptionConstants.GITHUB_AUTH_ERROR);
    }
}
