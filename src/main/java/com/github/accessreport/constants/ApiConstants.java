package com.github.accessreport.constants;

/**
 * Stores all API-related constant values.
 */
public class ApiConstants {

    public static final String BASE_URL = "https://api.github.com";
    public static final String ACCEPT_HEADER_VALUE = "application/vnd.github+json";

    public static final String BASE_API_PATH = "/api/github";
    public static final String ACCESS_REPORT_PATH = "/org/{orgName}/access-report";

    private ApiConstants() {
    }
}
