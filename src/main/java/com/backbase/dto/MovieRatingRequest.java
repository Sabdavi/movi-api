package com.backbase.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MovieRatingRequest(
        @NotBlank(message = "Title must not be blank")
        @Size(max = 255, message = "Title must be less than 255 characters")
        String title,

        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 10, message = "Rating must be no more than 10")
        int rate) {
}
