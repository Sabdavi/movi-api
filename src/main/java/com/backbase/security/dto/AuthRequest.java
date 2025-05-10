package com.backbase.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(

        @NotBlank(message = "Client ID must not be blank")
        @Size(max = 255, message = "Client ID must be between 1 and 255 characters")
        String clientId,

        @NotBlank(message = "Client secret must not be blank")
        @Size(min = 32, message = "Client secret must be grater than 32 characters")
        String clientSecret,

        @NotBlank(message = "Username must not be blank")
        @Size(min = 6, max = 100, message = "Username must be between 6 than 100 characters")
        String username,

        @NotBlank(message = "Password must not be blank")
        @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
        String password) {

}
