package com.github.accessreport.exception;

import com.github.accessreport.constants.ExceptionConstants;

/**
 * Custom exception thrown when a GitHub organization is not found.
 *
 * This typically occurs when:
 * 1. The provided organization name does not exist
 * 2. GitHub API returns HTTP 404 (Not Found)
 *
 * This exception extends RuntimeException for unchecked exception handling.
 */
public class OrganizationNotFoundException extends RuntimeException {

    public OrganizationNotFoundException(String orgName) {
        super(String.format(
                ExceptionConstants.ORGANIZATION_NOT_FOUND,
                orgName
        ));
    }
}
