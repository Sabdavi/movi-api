package com.backbase.dto;

import java.time.Instant;

public record MovieRatingResponse(String title, int rate, Instant createdAt) {
}
