package com.winwin.dataapi.transform;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransformController.class)
class TransformControllerTest {

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
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(transformService);
    }
}
