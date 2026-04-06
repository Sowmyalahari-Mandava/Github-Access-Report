package com.github.accessreport.config;

import com.github.accessreport.constants.ApiConstants;
import com.github.accessreport.constants.HeaderConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * Configuration class for defining application-level beans.
 * This class is responsible for:
 * 1. Creating a RestTemplate bean
 * 2. Adding an interceptor to include GitHub authentication headers
 */
@Configuration
public class AppConfig {

    @Value("${github.token}")
    private String githubToken;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Add interceptor to include headers in every request
        restTemplate.setInterceptors(
                Collections.singletonList(createAuthInterceptor())
        );

        return restTemplate;
    }

    private ClientHttpRequestInterceptor createAuthInterceptor() {
        return (request, body, execution) -> {

            request.getHeaders().set(
                    HttpHeaders.AUTHORIZATION,
                    HeaderConstants.AUTHORIZATION_PREFIX + githubToken
            );

            request.getHeaders().set(
                    HttpHeaders.ACCEPT,
                    ApiConstants.ACCEPT_HEADER_VALUE
            );

            return execution.execute(request, body);
        };
    }
}
