package com.github.accessreport.service;

import com.github.accessreport.constants.ApiConstants;
import com.github.accessreport.dto.AccessReportResponse;
import com.github.accessreport.enums.GitHubApiPaths;
import com.github.accessreport.exception.GitHubAuthException;
import com.github.accessreport.exception.OrganizationNotFoundException;
import com.github.accessreport.exception.RateLimitExceededException;
import com.github.accessreport.model.GitHubContributorResponse;
import com.github.accessreport.model.GitHubRepositoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Service class responsible for interacting with GitHub API
 * and generating access reports for organizations.
 *
 * Responsibilities:
 * 1. Fetch repositories of an organization
 * 2. Fetch contributors of each repository
 * 3. Aggregate and map users to their repositories
 * 4. Handle API errors and convert them into custom exceptions
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubService {

    private final RestTemplate restTemplate;

    private static final int PER_PAGE = 100;
    private static final String BOT_TYPE = "Bot";

    /**
     * Main method - builds the full access report for an organization.
     * Steps:
     *  1. Get all repos in the org
     *  2. For each repo, get the list of contributors
     *  3. Group repos by username
     */
    public AccessReportResponse getAccessReport(String orgName) {

        List<GitHubRepositoryResponse> repos = getRepositories(orgName);

        Map<String, List<String>> userToRepos = new HashMap<>();

        for (GitHubRepositoryResponse repo : repos) {
            List<GitHubContributorResponse> contributors = getContributors(orgName, repo.getName());

            for (GitHubContributorResponse contributor : contributors) {
                if (BOT_TYPE.equalsIgnoreCase(contributor.getType())) {
                    continue;
                }

                userToRepos
                        .computeIfAbsent(contributor.getLogin(), k -> new ArrayList<>())
                        .add(repo.getName());
            }
        }

        List<AccessReportResponse.UserAccess> userAccessList = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : userToRepos.entrySet()) {
            userAccessList.add(new AccessReportResponse.UserAccess(entry.getKey(), entry.getValue()));
        }

        userAccessList.sort(Comparator.comparing(AccessReportResponse.UserAccess::getUsername));

        return new AccessReportResponse(orgName, userAccessList);
    }

    /**
     * Fetches all repositories for the given organization.
     * Uses pagination (100 per page) to handle orgs with many repos.
     */
    private List<GitHubRepositoryResponse> getRepositories(String orgName) {
        List<GitHubRepositoryResponse> allRepos = new ArrayList<>();
        int page = 1;

        while (true) {
            String url = ApiConstants.BASE_URL +
                    GitHubApiPaths.ORG_REPOS.getPath(orgName)
                    + String.format(ApiConstants.QUERY_PARAMS, ApiConstants.PER_PAGE, page);

            try {
                GitHubRepositoryResponse[] pageResult = restTemplate.getForObject(url, GitHubRepositoryResponse[].class);

                if (pageResult == null || pageResult.length == 0) {
                    break;
                }

                allRepos.addAll(Arrays.asList(pageResult));

                if (pageResult.length < PER_PAGE) {
                    break;
                }

                page++;

            } catch (HttpClientErrorException ex) {
                handleHttpError(ex, orgName);
            }
        }

        log.info(ApiConstants.REPO_FETCH_LOG, allRepos.size(), orgName);
        return allRepos;
    }

    /**
     * Fetches all contributors for a single repository.
     * Uses pagination to handle repos with many contributors.
     */
    private List<GitHubContributorResponse> getContributors(String orgName, String repoName) {
        List<GitHubContributorResponse> allContributors = new ArrayList<>();
        int page = 1;

        while (true) {
            String url = ApiConstants.BASE_URL +
                    GitHubApiPaths.CONTRIBUTORS.getPath(orgName, repoName)
                    + String.format(ApiConstants.QUERY_PARAMS, ApiConstants.PER_PAGE, page);

            try {
                GitHubContributorResponse[] pageResult = restTemplate.getForObject(url, GitHubContributorResponse[].class);

                if (pageResult == null || pageResult.length == 0) {
                    break;
                }

                allContributors.addAll(Arrays.asList(pageResult));

                if (pageResult.length < PER_PAGE) {
                    break;
                }

                page++;

            } catch (HttpClientErrorException.NotFound ex) {
                log.warn(ApiConstants.CONTRIBUTORS_NOT_FOUND_LOG, orgName, repoName);
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
            throw ex;
        }
    }
}
