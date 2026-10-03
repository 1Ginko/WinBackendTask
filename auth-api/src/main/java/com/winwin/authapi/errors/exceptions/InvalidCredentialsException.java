package com.winwin.authapi.errors.exceptions;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class InvalidCredentialsException extends ResponseStatusException {

    public InvalidCredentialsException() {
        super(UNAUTHORIZED, "Invalid email or password");
    }
}