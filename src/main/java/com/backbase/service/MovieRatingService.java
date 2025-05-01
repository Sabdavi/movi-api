package com.backbase.service;

import com.backbase.dto.MovieRatingRequest;
import com.backbase.entity.MovieRating;
import com.backbase.repository.MovieRatingRepository;
import org.springframework.stereotype.Service;

@Service
public class MovieRatingService {
    private final MovieRatingRepository movieRatingRepository;

    public MovieRatingService(MovieRatingRepository movieRepository) {
        this.movieRatingRepository = movieRepository;
    }

    public MovieRating rateMovie(MovieRatingRequest movieRatingRequest) {
        MovieRating movieRating = new MovieRating(movieRatingRequest.title(), movieRatingRequest.rate());
        return movieRatingRepository.save(movieRating);
    }
}
