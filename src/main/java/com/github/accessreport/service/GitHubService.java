package com.github.accessreport.service;

import com.github.accessreport.dto.AccessReportResponse;
import com.github.accessreport.exception.GitHubAuthException;
import com.github.accessreport.exception.OrganizationNotFoundException;
import com.github.accessreport.exception.RateLimitExceededException;
import com.github.accessreport.model.GitHubContributor;
import com.github.accessreport.model.GitHubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubService {

    private final RestTemplate restTemplate;

    private static final String GITHUB_API = "https://api.github.com";

    /**
     * Main method - builds the full access report for an organization.
     * Steps:
     *  1. Get all repos in the org
     *  2. For each repo, get the list of contributors
     *  3. Group repos by username
     */
    public AccessReportResponse getAccessReport(String orgName) {

        // Step 1: Get all repositories of the org
        List<GitHubRepository> repos = getRepositories(orgName);

        // Step 2: Map each user to the repos they contributed to
        // Key = username, Value = list of repo names
        Map<String, List<String>> userToRepos = new HashMap<>();

        for (GitHubRepository repo : repos) {
            List<GitHubContributor> contributors = getContributors(orgName, repo.getName());

            for (GitHubContributor contributor : contributors) {
                // Skip bots, only include real users
                if ("Bot".equalsIgnoreCase(contributor.getType())) {
                    continue;
                }

                // Add this repo to the user's list
                userToRepos
                    .computeIfAbsent(contributor.getLogin(), k -> new ArrayList<>())
                    .add(repo.getName());
            }
        }

        // Step 3: Convert the map to a list of UserAccess objects
        List<AccessReportResponse.UserAccess> userAccessList = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : userToRepos.entrySet()) {
            userAccessList.add(new AccessReportResponse.UserAccess(entry.getKey(), entry.getValue()));
        }

        // Sort by username so output is consistent
        userAccessList.sort(Comparator.comparing(AccessReportResponse.UserAccess::getUsername));

        return new AccessReportResponse(orgName, userAccessList);
    }

    /**
     * Fetches all repositories for the given organization.
     * Uses pagination (100 per page) to handle orgs with many repos.
     */
    private List<GitHubRepository> getRepositories(String orgName) {
        List<GitHubRepository> allRepos = new ArrayList<>();
        int page = 1;

        while (true) {
            String url = GITHUB_API + "/orgs/" + orgName + "/repos?per_page=100&page=" + page;

            try {
                GitHubRepository[] pageResult = restTemplate.getForObject(url, GitHubRepository[].class);

                // If page is empty or null, we've fetched everything
                if (pageResult == null || pageResult.length == 0) {
                    break;
                }

                allRepos.addAll(Arrays.asList(pageResult));

                // If we got less than 100, this was the last page
                if (pageResult.length < 100) {
                    break;
                }

                page++;

            } catch (HttpClientErrorException ex) {
                handleHttpError(ex, orgName);
            }
        }

        log.info("Fetched {} repositories for org: {}", allRepos.size(), orgName);
        return allRepos;
    }

    /**
     * Fetches all contributors for a single repository.
     * Uses pagination to handle repos with many contributors.
     */
    private List<GitHubContributor> getContributors(String orgName, String repoName) {
        List<GitHubContributor> allContributors = new ArrayList<>();
        int page = 1;

        while (true) {
            String url = GITHUB_API + "/repos/" + orgName + "/" + repoName
                    + "/contributors?per_page=100&page=" + page;

            try {
                GitHubContributor[] pageResult = restTemplate.getForObject(url, GitHubContributor[].class);

                if (pageResult == null || pageResult.length == 0) {
                    break;
                }

                allContributors.addAll(Arrays.asList(pageResult));

                if (pageResult.length < 100) {
                    break;
                }

                page++;

            } catch (HttpClientErrorException.NotFound ex) {
                // Repo may have been deleted — just skip it
                log.warn("Repo {}/{} not found when fetching contributors. Skipping.", orgName, repoName);
                break;
            } catch (HttpClientErrorException ex) {
                handleHttpError(ex, orgName);
            }
        }

        return allContributors;
    }

    /**
     * Converts HTTP error codes from GitHub into our custom exceptions.
     * This gives users a clear, readable error message.
     */
    private void handleHttpError(HttpClientErrorException ex, String context) {
        int statusCode = ex.getStatusCode().value();

        if (statusCode == 401) {
            throw new GitHubAuthException();
        } else if (statusCode == 403) {
            throw new RateLimitExceededException();
        } else if (statusCode == 404) {
            throw new OrganizationNotFoundException(context);
        } else if (statusCode == 429) {
            throw new RateLimitExceededException();
        } else {
            // For anything else, just rethrow
            throw ex;
        }
    }
}
