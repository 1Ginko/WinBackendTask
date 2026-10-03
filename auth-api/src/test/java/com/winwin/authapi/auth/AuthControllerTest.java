package com.winwin.authapi.auth;

import com.winwin.authapi.config.SecurityConfig;
import com.winwin.authapi.errors.exceptions.EmailAlreadyExistsException;
import com.winwin.authapi.errors.exceptions.InvalidCredentialsException;
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
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("User with this email already exists"))
                .andExpect(jsonPath("$.path").value("/api/auth/register"))
                .andExpect(jsonPath("$.fieldErrors").doesNotExist());
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.path").value("/api/auth/register"))
                .andExpect(jsonPath("$.fieldErrors.password").value("Password must not be blank"));

        verifyNoInteractions(authService);
    }

    @Test
    void rejectsPasswordLongerThanMaximumLength() throws Exception {
        String password = "a".repeat(25);
        String requestJson = """
                {
                  "email": "oleg@example.com",
                  "password": "%s"
                }
                """.formatted(password);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.password")
                        .value("Password must not exceed 24 characters"));

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
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid email or password"))
                .andExpect(jsonPath("$.path").value("/api/auth/login"))
                .andExpect(jsonPath("$.fieldErrors").doesNotExist());
    }
}
