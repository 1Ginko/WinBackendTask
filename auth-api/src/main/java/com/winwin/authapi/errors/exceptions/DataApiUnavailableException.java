package com.winwin.authapi.errors.exceptions;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;

public class DataApiUnavailableException extends ResponseStatusException {

    public DataApiUnavailableException() {
        super(BAD_GATEWAY, "Data API returned an invalid response");
    }

    public DataApiUnavailableException(Throwable cause) {
        super(BAD_GATEWAY, "Data API is unavailable", cause);
    }
}