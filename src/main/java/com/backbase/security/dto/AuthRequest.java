package com.backbase.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(

        @NotBlank(message = "Client ID must not be blank")
        @Size(max = 255, message = "Client ID must be between 1 and 255 characters")
        String clientId,

        @NotBlank(message = "Client secret must not be blank")
        @Size(min = 32, message = "Client secret must be grater than 32 characters")
        String clientSecret) {
}
