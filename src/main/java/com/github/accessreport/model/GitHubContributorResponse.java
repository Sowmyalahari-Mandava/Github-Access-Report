package com.github.accessreport.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Model class representing a contributor returned by GitHub API.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubContributorResponse {

    private String login;
    private int contributions;
    private String type;
}
