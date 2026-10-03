package com.winwin.authapi.errors.exceptions;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.CONFLICT;

public class EmailAlreadyExistsException extends ResponseStatusException {

    public EmailAlreadyExistsException() {
        super(CONFLICT, "User with this email already exists");
    }
}