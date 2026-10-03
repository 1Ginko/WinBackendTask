package com.winwin.authapi.auth;

import com.winwin.authapi.data.entity.UserEntity;
import com.winwin.authapi.data.repository.UserRepository;
import com.winwin.authapi.errors.exceptions.EmailAlreadyExistsException;
import com.winwin.authapi.errors.exceptions.InvalidCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public void register(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        String passwordHash = passwordEncoder.encode(password);
        int insertedRows = userRepository.insertUser(UUID.randomUUID(), normalizedEmail, passwordHash);
        if (insertedRows == 0) throw new EmailAlreadyExistsException();
    }

    @Transactional(readOnly = true)
    public String login(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        UserEntity user = userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(InvalidCredentialsException::new);
        boolean passwordMatches = passwordEncoder.matches(password, user.getPasswordHash());
        if (!passwordMatches) throw new InvalidCredentialsException();

        return jwtService.createToken(user.getId(), user.getEmail());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
