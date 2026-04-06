package com.github.accessreport.controller;

import com.github.accessreport.constants.ApiConstants;
import com.github.accessreport.dto.AccessReportResponse;
import com.github.accessreport.service.GitHubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for handling GitHub access report requests.
 * This controller exposes endpoints to fetch access reports
 * for a given GitHub organization.
 */
@RestController
@RequestMapping(ApiConstants.BASE_API_PATH)
@RequiredArgsConstructor
public class AccessReportController {

    private final GitHubService gitHubService;

    @GetMapping(ApiConstants.ACCESS_REPORT_PATH)
    public ResponseEntity<AccessReportResponse> getAccessReport(
            @PathVariable String orgName) {

        AccessReportResponse report = gitHubService.getAccessReport(orgName);

        return ResponseEntity.ok(report);
    }
}
