package com.winwin.authapi.processing;

import com.winwin.authapi.errors.exceptions.DataApiUnavailableException;
import com.winwin.authapi.processing.models.requests.ProcessRequest;
import com.winwin.authapi.processing.models.responses.ProcessResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class DataApiClient {

    private final RestClient restClient;

    public DataApiClient(@Qualifier("dataApiRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public String transform(String text) {
        try {
            ProcessResponse response = restClient.post()
                    .uri("/api/transform")
                    .body(new ProcessRequest(text))
                    .retrieve()
                    .body(ProcessResponse.class);

            if (response == null || response.result() == null) {
                throw new DataApiUnavailableException();
            }

            return response.result();
        } catch (RestClientException exception) {
            throw new DataApiUnavailableException(exception);
        }
    }
}