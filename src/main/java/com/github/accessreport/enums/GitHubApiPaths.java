package com.github.accessreport.enums;

/**
 * Enum representing GitHub API endpoints.
 */
public enum GitHubApiPaths {

    ORG_REPOS("/orgs/%s/repos"),
    CONTRIBUTORS("/repos/%s/%s/contributors");

    private final String path;

    GitHubApiPaths(String path) {
        this.path = path;
    }

    public String getPath(Object... args) {
        return String.format(path, args);
    }
}
