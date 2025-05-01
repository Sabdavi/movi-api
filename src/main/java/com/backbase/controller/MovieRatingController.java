package com.backbase.controller;

import com.backbase.dto.MovieAverageRating;
import com.backbase.dto.MovieRatingRequest;
import com.backbase.dto.MovieRatingResponse;
import com.backbase.entity.MovieRating;
import com.backbase.projection.AverageRatingProjection;
import com.backbase.service.MovieRatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        if(!isValidRate(rate)) {
            throw new IllegalArgumentException("Rating must be between 1 and 10.");
        }

        MovieRating movieRating = movieRatingService.rateMovie(movieRatingRequest);
        return ResponseEntity.ok(new MovieRatingResponse(movieRating.getTitle(), movieRating.getRate(), movieRating.getCreatedAt()));
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<MovieAverageRating>> getTop10Rating() {
        List<AverageRatingProjection> topAverageRatings = movieRatingService.getTop10TopRatedMovies();
        List<MovieAverageRating> movieAverageRatings = topAverageRatings.stream().map(movieRatingService::toDto).toList();
        return ResponseEntity.ok(movieAverageRatings);
    }

    private boolean isValidRate(int rate) {
        return rate > 0 && rate < 11 ;
    }
}
