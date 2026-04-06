package com.github.accessreport.constants;

/**
 * Stores all exception-related constant messages.
 */
public class ExceptionConstants {

    public static final String GITHUB_AUTH_ERROR =
            "GitHub authentication failed. Check that GITHUB_TOKEN is set and valid.";
    public static final String GITHUB_CONNECTION_ERROR =
            "Could not connect to GitHub. Please check your internet connection.";
    public static final String GENERIC_ERROR_PREFIX =
            "Something went wrong: ";
    public static final String ORGANIZATION_NOT_FOUND =
            "Organization '%s' not found on GitHub.";
    public static final String RATE_LIMIT_EXCEEDED =
            "GitHub API rate limit exceeded. Please wait and try again, or use an authenticated token.";

    private ExceptionConstants() {
    }
}
