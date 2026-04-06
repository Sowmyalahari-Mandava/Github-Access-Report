package com.github.accessreport.constants;

/**
 * Stores all API-related constant values.
 */
public class ApiConstants {

    public static final String BASE_URL = "https://api.github.com";
    public static final String ACCEPT_HEADER_VALUE = "application/vnd.github+json";
    public static final String BASE_API_PATH = "/api/github";
    public static final String ACCESS_REPORT_PATH = "/org/{orgName}/access-report";
    public static final int PER_PAGE = 100;
    public static final String BOT_TYPE = "Bot";
    public static final String CONTRIBUTORS_NOT_FOUND_LOG =
            "Repo {}/{} not found when fetching contributors. Skipping.";
    public static final String QUERY_PARAMS =
            "?per_page=%d&page=%d";
    public static final String REPO_FETCH_LOG =
            "Fetched {} repositories for org: {}";
    public static final String STATUS = "status";
    public static final String ERROR = "error";
    public static final String MESSAGE = "message";
    public static final String TIMESTAMP = "timestamp";

    private ApiConstants() {
    }
}
