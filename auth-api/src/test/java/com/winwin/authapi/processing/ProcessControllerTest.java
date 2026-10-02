package com.winwin.authapi.processing;

import com.winwin.authapi.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProcessController.class)
@Import(SecurityConfig.class)
class ProcessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProcessService processService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void processesTextForAuthenticatedUser() throws Exception {
        UUID userId = UUID.randomUUID();
        given(processService.process(userId, "hello")).willReturn("OLLEH");

        String requestJson = """
                {
                  "text": "hello"
                }
                """;

        mockMvc.perform(post("/api/process")
                        .with(jwt().jwt(token -> token.subject(userId.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("OLLEH"));

        then(processService).should().process(userId, "hello");
    }

    @Test
    void rejectsRequestWithoutJwt() throws Exception {
        mockMvc.perform(post("/api/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "text": "hello"
                                }
                                """))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(processService);
    }

    @Test
    void rejectsBlankText() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(post("/api/process")
                        .with(jwt().jwt(token -> token.subject(userId.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "text": " "
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(processService);
    }
}
