package com.winwin.authapi.processing;

import com.winwin.authapi.processing.models.requests.ProcessRequest;
import com.winwin.authapi.processing.models.responses.ProcessResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class DataApiClient {

    private final RestClient restClient;

    public DataApiClient(@Qualifier("dataApiRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public String transform(String text) {
        ProcessResponse response = restClient.post()
                .uri("/api/transform")
                .body(new ProcessRequest(text))
                .retrieve()
                .body(ProcessResponse.class);

        if (response == null || response.result() == null) {
            throw new IllegalStateException("Data API response is null");
        }

        return response.result();
    }
}