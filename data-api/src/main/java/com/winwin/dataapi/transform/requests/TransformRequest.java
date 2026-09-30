package com.winwin.dataapi.transform.requests;

import jakarta.validation.constraints.NotBlank;

public record TransformRequest(
        @NotBlank(message = "Text must not be blank")
        String text
) { }