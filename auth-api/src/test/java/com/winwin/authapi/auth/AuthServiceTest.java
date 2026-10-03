package com.winwin.authapi.auth;

import com.winwin.authapi.data.entity.UserEntity;
import com.winwin.authapi.data.repository.UserRepository;
import com.winwin.authapi.errors.exceptions.EmailAlreadyExistsException;
import com.winwin.authapi.errors.exceptions.InvalidCredentialsException;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class AuthServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final AuthService authService = new AuthService(
            userRepository,
            passwordEncoder,
            jwtService
    );

    @Test
    void registersNormalizedEmailAndEncodedPassword() {
        given(passwordEncoder.encode("pass")).willReturn("encoded-password");
        given(userRepository.insertUser(
                any(UUID.class),
                eq("oleg@example.com"),
                eq("encoded-password")
        )).willReturn(1);

        authService.register(" OLEG@Example.com ", "pass");

        then(userRepository).should().insertUser(
                any(UUID.class),
                eq("oleg@example.com"),
                eq("encoded-password")
        );
    }

    @Test
    void rejectsDuplicateEmail() {
        given(passwordEncoder.encode("pass")).willReturn("encoded-password");
        given(userRepository.insertUser(
                any(UUID.class),
                eq("oleg@example.com"),
                eq("encoded-password")
        )).willReturn(0);

        assertThatThrownBy(() -> authService.register("oleg@example.com", "pass"))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void returnsTokenForValidCredentials() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity(
                userId,
                "oleg@example.com",
                "encoded-password"
        );
        given(userRepository.findByEmail("oleg@example.com"))
                .willReturn(Optional.of(user));
        given(passwordEncoder.matches("pass", "encoded-password"))
                .willReturn(true);
        given(jwtService.createToken(userId, "oleg@example.com"))
                .willReturn("jwt-token");

        String token = authService.login(" OLEG@Example.com ", "pass");

        assertThat(token).isEqualTo("jwt-token");
    }

    @Test
    void rejectsUnknownEmail() {
        given(userRepository.findByEmail("unknown@example.com"))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login("unknown@example.com", "pass"))
                .isInstanceOf(InvalidCredentialsException.class);

        verifyNoInteractions(jwtService);
    }

    @Test
    void rejectsInvalidPassword() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity(
                userId,
                "oleg@example.com",
                "encoded-password"
        );
        given(userRepository.findByEmail("oleg@example.com"))
                .willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrong-password", "encoded-password"))
                .willReturn(false);

        assertThatThrownBy(() -> authService.login("oleg@example.com", "wrong-password"))
                .isInstanceOf(InvalidCredentialsException.class);

        verifyNoInteractions(jwtService);
    }
}
