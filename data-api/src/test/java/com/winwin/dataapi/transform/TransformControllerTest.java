package com.winwin.dataapi.transform;

import com.winwin.dataapi.transform.config.WebConfig;
import com.winwin.dataapi.transform.security.InternalTokenInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = TransformController.class,
        properties = "app.internal-token=test-internal-token"
)
@Import({WebConfig.class, InternalTokenInterceptor.class})
class TransformControllerTest {

    private static final String TOKEN_HEADER = "X-Internal-Token";
    private static final String VALID_TOKEN = "test-internal-token";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransformService transformService;

    @Test
    void transformsText() throws Exception {
        given(transformService.transform("hello")).willReturn("OLLEH");
        String requestJson = """
                {
                  "text": "hello"
                }
                """;

        mockMvc.perform(post("/api/transform")
                        .header(TOKEN_HEADER, VALID_TOKEN)
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("OLLEH"));
    }

    @Test
    void rejectsBlankText() throws Exception {
        String requestJson = """
                {
                  "text": " "
                }
                """;

        mockMvc.perform(post("/api/transform")
                        .header(TOKEN_HEADER, VALID_TOKEN)
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(transformService);
    }

    @Test
    void rejectsRequestWithoutInternalToken() throws Exception {
        mockMvc.perform(post("/api/transform"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(transformService);
    }

    @Test
    void rejectsRequestWithInvalidInternalToken() throws Exception {
        mockMvc.perform(
                post("/api/transform").header(TOKEN_HEADER, "invalid-token")
        ).andExpect(
                status().isForbidden()
        );

        verifyNoInteractions(transformService);
    }
}
