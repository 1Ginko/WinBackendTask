package com.winwin.authapi.processing.models.requests;

import jakarta.validation.constraints.NotBlank;

public record ProcessRequest(
        @NotBlank(message = "Text must not be blank")
        String text
) { }