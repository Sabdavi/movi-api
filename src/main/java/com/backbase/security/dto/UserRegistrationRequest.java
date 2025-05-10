package com.backbase.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(

        @NotBlank(message = "Username must not be blank")
        @Size(min = 6, max = 100, message = "Username must be between 6 than 100 characters")
        String username,

        @NotBlank(message = "Password must not be blank")
        @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
        String password) {
}
