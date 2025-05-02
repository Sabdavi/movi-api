package com.backbase.dto;

public record MovieAverageRating(String title, double averageRate, Long boxOffice) {
    public MovieAverageRating(String title, double averageRate) {
        this(title, averageRate, null);
    }
}
