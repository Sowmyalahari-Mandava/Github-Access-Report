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
git clone https://github.com/your-username/github-access-report.git
cd github-access-report
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

App starts on: `http://localhost:8080`

---

## How Authentication Works

- The app uses a **GitHub Personal Access Token (PAT)** for authentication.
- The token is read from the `GITHUB_TOKEN` environment variable.
- It is never hardcoded anywhere in the code.
- The token is sent as a `Bearer` token in the `Authorization` header on every request.

### How to create a token:
1. Go to https://github.com/settings/tokens
2. Click **Generate new token**
3. Select scopes: `repo`, `read:org`
4. Copy the token and set it as `GITHUB_TOKEN`

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

```json
{
  "organization": "spring-projects",
  "users": [
    {
      "username": "jhoeller",
      "repositories": [
        "spring-framework",
        "spring-boot"
      ]
    },
    {
      "username": "bclozel",
      "repositories": [
        "spring-framework"
      ]
    }
  ]
}
```

### Error Response

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Organization 'xyz' not found on GitHub.",
  "timestamp": "2026-04-04T10:00:00"
}
```

---

## Assumptions & Design Decisions

1. **Contributors API** is used instead of Collaborators API
   - The collaborators endpoint requires admin access on private repos
   - Contributors endpoint is public and works for all repos

2. **Pagination** is implemented
   - GitHub returns max 100 items per page
   - The app loops through all pages to get complete data

3. **Bots are excluded** from the report
   - GitHub Apps and bot accounts have `type = "Bot"` in the API response
   - These are filtered out so only real users appear

4. **RestTemplate** is used for HTTP calls
   - Simple and easy to understand
   - Built into Spring Boot, no extra dependencies needed

5. **Custom exceptions** give clear error messages
   - `OrganizationNotFoundException` → 404
   - `GitHubAuthException` → 401
   - `RateLimitExceededException` → 429
