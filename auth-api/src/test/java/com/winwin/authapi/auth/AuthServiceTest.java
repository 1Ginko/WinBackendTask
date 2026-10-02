package com.winwin.authapi.auth;

import com.winwin.authapi.data.repository.UserRepository;
import com.winwin.authapi.exceptions.EmailAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class AuthServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final AuthService authService = new AuthService(userRepository, passwordEncoder);

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
}
