package com.github.accessreport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing the response
 * for GitHub organization access report.
 * This class is returned to the client when the API is called.
 */
@Data
@AllArgsConstructor
public class AccessReportResponse {

    private String organization;
    private List<UserAccess> users;

    /**
     * Inner DTO class representing a user's access details.
     * Contains:
     * 1. Username of the GitHub user
     * 2. List of repositories the user has access to
     */
    @Data
    @AllArgsConstructor
    public static class UserAccess {

        private String username;
        private List<String> repositories;
    }
}