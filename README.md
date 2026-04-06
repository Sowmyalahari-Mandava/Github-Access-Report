# GitHub Access Report

A Spring Boot application that connects to the GitHub API and generates a report
showing which users have access to which repositories in a given organization.

---

## How to Run

### Prerequisites
- Java 17+
- Maven
- A GitHub Personal Access Token

### Steps

**1. Clone the project**
```bash
git clone https://github.com/Sowmyalahari-Mandava/Github-Access-Report.git
cd Github-Access-Report
```

**2. Set your GitHub token as an environment variable**

Linux / Mac:
```bash
export GITHUB_TOKEN=your_token_here
```

Windows CMD:
```cmd
set GITHUB_TOKEN=your_token_here
```

**3. Run the application**
```bash
mvn spring-boot:run
```

App starts on: http://localhost:8080

---

## How Authentication Works

- The app uses a GitHub Personal Access Token (PAT) for authentication.
- The token is read from the GITHUB_TOKEN environment variable.
- It is never hardcoded anywhere in the code.
- The token is sent as a Bearer token in the Authorization header on every request.

### How to create a token:
1. Go to https://github.com/settings/tokens
2. Click Generate new token
3. Select scopes: repo, read:org
4. Copy the token and set it as GITHUB_TOKEN

---

## API Endpoint

```
GET /api/github/org/{orgName}/access-report
```

### Example

```bash
curl http://localhost:8080/api/github/org/spring-projects/access-report
```

### Success Response

Returns a JSON object with the organization name and a list of users, each with their accessible repositories.

    {
      "organization": "spring-projects",
      "users": [
        {
          "username": "jhoeller",
          "repositories": ["spring-framework", "spring-boot"]
        },
        {
          "username": "bclozel",
          "repositories": ["spring-framework"]
        }
      ]
    }

### Error Response

Returns a JSON object with the HTTP status code, error type, a descriptive message, and a timestamp.

    {
      "status": 404,
      "error": "Not Found",
      "message": "Organization 'xyz' not found on GitHub.",
      "timestamp": "2026-04-04T10:00:00"
    }

---

## Constants & Enums

### AppConstants
- All hardcoded values such as GitHub API base URL, endpoint paths, header names, media types, pagination defaults, and error message templates are defined in a single AppConstants class.
- This ensures no magic strings or numbers are scattered across the codebase.

### PermissionLevel (Enum)
- Represents the three access levels a user can have on a repository: ADMIN, WRITE, and READ.
- Used throughout the service and response layers to avoid hardcoded string comparisons.

### ReportStatus (Enum)
- Represents the outcome of a report generation attempt: SUCCESS, PARTIAL, or FAILED.
- Included in the API response so callers know whether the data returned is complete.

---

## Custom Exceptions

Custom exceptions replace generic errors and give clear, meaningful messages at every failure point.

- **OrganizationNotFoundException** — Thrown when the given organization name does not exist on GitHub. Returns HTTP 404.
- **GitHubAuthException** — Thrown when the token is missing, expired, or does not have the required scopes. Returns HTTP 401.
- **RateLimitExceededException** — Thrown when GitHub API rate limit is hit during report generation. Returns HTTP 429.
- **GitHubApiException** — Thrown for any other unexpected error received from the GitHub API. Returns HTTP 502.

---

## Global Exception Handler

- A single GlobalExceptionHandler class annotated with @RestControllerAdvice intercepts all exceptions thrown anywhere in the application.
- Each custom exception is mapped to the correct HTTP status code.
- A fallback handler catches any unhandled Exception and returns HTTP 500 with a generic message, so internal details are never leaked to the client.
- All error responses follow the same JSON structure with status, error, message, and timestamp fields, making it easy for API consumers to handle errors consistently.

---

## Assumptions & Design Decisions

1. **Contributors API** is used instead of Collaborators API
    - The collaborators endpoint requires admin access on private repos
    - Contributors endpoint is public and works for all repos

2. **Pagination** is implemented
    - GitHub returns max 100 items per page
    - The app loops through all pages to get complete data

3. **Bots are excluded** from the report
    - GitHub Apps and bot accounts have type Bot in the API response
    - These are filtered out so only real users appear

4. **RestTemplate** is used for HTTP calls
    - Simple and easy to understand
    - Built into Spring Boot, no extra dependencies needed

5. **Custom exceptions** give clear error messages
    - OrganizationNotFoundException returns 404
    - GitHubAuthException returns 401
    - RateLimitExceededException returns 429
