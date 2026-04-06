package com.github.accessreport.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Model class representing a repository returned by GitHub API.
 *
 * This class maps the JSON response received from GitHub
 * for organization repositories.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubRepositoryResponse {

    private String name;
    private String fullName;
    private boolean isPrivate;
}
