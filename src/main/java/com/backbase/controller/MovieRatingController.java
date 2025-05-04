package com.backbase.controller;

import com.backbase.dto.MovieAverageRating;
import com.backbase.dto.MovieRatingRequest;
import com.backbase.dto.MovieRatingResponse;
import com.backbase.entity.MovieRating;
import com.backbase.exception.InvalidRatingException;
import com.backbase.exception.MovieNotFoundException;
import com.backbase.service.MovieRatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
@Tag(name = "Movie Ratings", description = "Endpoints for rating movies and listing top-rated ones")
public class MovieRatingController {

    private final MovieRatingService movieRatingService;

    public MovieRatingController(MovieRatingService movieRatingService) {
        this.movieRatingService = movieRatingService;
    }

    @Operation(summary = "Rate a movie between 1 and 10")
    @PostMapping("/rate")
    public ResponseEntity<MovieRatingResponse> rateMovie(@RequestBody MovieRatingRequest movieRatingRequest) {

        int rate = movieRatingRequest.rate();
        if (!isValidRate(rate)) {
            throw new InvalidRatingException("Rating must be between 1 and 10.");
        }
        String title = movieRatingRequest.title();
        if(!isValidTitle(title)) {
            throw new MovieNotFoundException("Movie not found!");
        }

        MovieRating movieRating = movieRatingService.rateMovie(movieRatingRequest);
        return ResponseEntity.ok(new MovieRatingResponse(movieRating.getTitle(), movieRating.getRate(), movieRating.getCreatedAt()));
    }

    @Operation(summary = "Get top 10 rated movies, sorted by rating and box office")
    @GetMapping("/top-rated")
    public ResponseEntity<List<MovieAverageRating>> getTop10Rating() {
        List<MovieAverageRating> topAverageRatings = movieRatingService.getTop10TopRatedMovies();
        return ResponseEntity.ok(topAverageRatings);
    }

    private boolean isValidRate(int rate) {
        return rate > 0 && rate < 11;
    }
    private boolean isValidTitle(String title) {
        return movieRatingService.isValidTitle(title);
    }

}
