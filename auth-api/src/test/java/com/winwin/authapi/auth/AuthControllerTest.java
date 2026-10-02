package com.winwin.authapi.auth;

import com.winwin.authapi.config.SecurityConfig;
import com.winwin.authapi.exceptions.EmailAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void registersUser() throws Exception {
        String requestJson = """
                {
                  "email": "oleg@example.com",
                  "password": "pass"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());

        then(authService).should().register("oleg@example.com", "pass");
    }

    @Test
    void rejectsDuplicateEmail() throws Exception {
        willThrow(new EmailAlreadyExistsException())
                .given(authService)
                .register("oleg@example.com", "pass");

        String requestJson = """
                {
                  "email": "oleg@example.com",
                  "password": "pass"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict());
    }

    @Test
    void rejectsBlankPassword() throws Exception {
        String requestJson = """
                {
                  "email": "oleg@example.com",
                  "password": " "
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }
}
