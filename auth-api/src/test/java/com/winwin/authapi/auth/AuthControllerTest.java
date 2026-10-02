package com.winwin.authapi.auth;

import com.winwin.authapi.config.SecurityConfig;
import com.winwin.authapi.exceptions.EmailAlreadyExistsException;
import com.winwin.authapi.exceptions.InvalidCredentialsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

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

    @Test
    void logsInUser() throws Exception {
        given(authService.login("oleg@example.com", "pass"))
                .willReturn("jwt-token");

        String requestJson = """
                {
                  "email": "oleg@example.com",
                  "password": "pass"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void rejectsInvalidCredentials() throws Exception {
        given(authService.login("oleg@example.com", "wrong-password"))
                .willThrow(new InvalidCredentialsException());

        String requestJson = """
                {
                  "email": "oleg@example.com",
                  "password": "wrong-password"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnauthorized());
    }
}
