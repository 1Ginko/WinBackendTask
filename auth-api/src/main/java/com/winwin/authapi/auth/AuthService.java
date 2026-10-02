package com.winwin.authapi.auth;

import com.winwin.authapi.data.repository.UserRepository;
import com.winwin.authapi.exceptions.EmailAlreadyExistsException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(String email, String password) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        String passwordHash = passwordEncoder.encode(password);
        int insertedRows = userRepository.insertUser(UUID.randomUUID(), normalizedEmail, passwordHash);
        if (insertedRows == 0) throw new EmailAlreadyExistsException();
    }
}
