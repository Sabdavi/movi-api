package com.backbase.controller;

import com.backbase.dto.MovieRatingRequest;
import com.backbase.dto.MovieRatingResponse;
import com.backbase.entity.MovieRating;
import com.backbase.service.MovieRatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movies")
public class MovieRatingController {

    private final MovieRatingService movieRatingService;

    public MovieRatingController(MovieRatingService movieRatingService) {
        this.movieRatingService = movieRatingService;
    }

    @PostMapping("/rate")
    public ResponseEntity<MovieRatingResponse> rateMovie(@RequestBody MovieRatingRequest movieRatingRequest) {

        int rate = movieRatingRequest.rate();
        if(rate > 10 || rate < 1) {
            throw new IllegalArgumentException("Rating must be between 1 and 10.");
        }

        MovieRating movieRating = movieRatingService.rateMovie(movieRatingRequest);
        return ResponseEntity.ok(new MovieRatingResponse(movieRating.getTitle(), movieRating.getRate(), movieRating.getCreatedAt()));
    }
}
