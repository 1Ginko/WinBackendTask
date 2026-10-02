package com.winwin.authapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
public class DataApiConfig {

    private static final String INTERNAL_TOKEN_HEADER = "X-Internal-Token";

    @Bean
    public RestClient dataApiRestClient(
            RestClient.Builder builder,
            @Value("${app.data-api.base-url}") String baseUrl,
            @Value("${app.data-api.internal-token}") String internalToken
    ) {
        return builder
                .baseUrl(baseUrl)
                .defaultHeader(INTERNAL_TOKEN_HEADER, internalToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }
}