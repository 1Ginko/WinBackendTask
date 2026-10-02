package com.winwin.authapi.exceptions;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class InvalidCredentialsException extends ResponseStatusException {

    public InvalidCredentialsException() {
        super(UNAUTHORIZED, "Invalid email or password");
    }
}