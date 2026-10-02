package com.winwin.authapi.auth.models.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static com.winwin.authapi.Const.EMAIL_MAX_LENGTH;
import static com.winwin.authapi.Const.PASSWORD_MAX_LENGTH;

public record RegisterRequest(

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be valid")
        @Size(
                max = EMAIL_MAX_LENGTH,
                message = "Email must not exceed " + EMAIL_MAX_LENGTH + " characters"
        )
        String email,

        @NotBlank(message = "Password must not be blank")
        @Size(
                max = PASSWORD_MAX_LENGTH,
                message = "Password must not exceed "
                        + PASSWORD_MAX_LENGTH
                        + " characters"
        )
        String password
) { }
