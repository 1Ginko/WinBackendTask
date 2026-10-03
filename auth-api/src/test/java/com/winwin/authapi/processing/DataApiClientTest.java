package com.winwin.authapi.processing;

import com.winwin.authapi.config.DataApiConfig;
import com.winwin.authapi.errors.exceptions.DataApiUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class DataApiClientTest {

    @Test
    void sendsInternalTokenAndReturnsTransformedText() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = new DataApiConfig().dataApiRestClient(
                builder,
                "http://localhost:8081",
                "internal-token"
        );
        DataApiClient dataApiClient = new DataApiClient(restClient);

        server.expect(once(), requestTo("http://localhost:8081/api/transform"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Internal-Token", "internal-token"))
                .andExpect(content().json("""
                        {
                          "text": "hello"
                        }
                        """))
                .andRespond(withSuccess("""
                        {
                          "result": "OLLEH"
                        }
                        """, MediaType.APPLICATION_JSON));

        String result = dataApiClient.transform("hello");

        assertThat(result).isEqualTo("OLLEH");
        server.verify();
    }

    @Test
    void throwsBadGatewayWhenDataApiReturnsServerError() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = new DataApiConfig().dataApiRestClient(
                builder,
                "http://localhost:8081",
                "internal-token"
        );
        DataApiClient dataApiClient = new DataApiClient(restClient);

        server.expect(once(), requestTo("http://localhost:8081/api/transform"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        assertThatThrownBy(() -> dataApiClient.transform("hello"))
                .isInstanceOf(DataApiUnavailableException.class)
                .satisfies(exception ->
                        assertThat(((DataApiUnavailableException) exception).getStatusCode())
                                .isEqualTo(HttpStatus.BAD_GATEWAY)
                );

        server.verify();
    }
}
