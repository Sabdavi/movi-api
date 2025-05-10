package com.backbase.controller;

import com.backbase.dto.MovieAverageRating;
import com.backbase.dto.MovieRatingRequest;
import com.backbase.dto.MovieRatingResponse;
import com.backbase.entity.MovieRating;
import com.backbase.service.MovieRatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('movies:rate')")
    @PostMapping("/rate")
    public ResponseEntity<MovieRatingResponse> rateMovie(@Valid @RequestBody MovieRatingRequest movieRatingRequest) {
        MovieRating movieRating = movieRatingService.rateMovie(movieRatingRequest);
        return ResponseEntity.ok(new MovieRatingResponse(movieRating.getTitle(), movieRating.getRate(), movieRating.getCreatedAt()));
    }

    @Operation(summary = "Get top 10 rated movies, sorted by rating and box office")
    @PreAuthorize("hasAuthority('movies:read')")
    @GetMapping("/top-rated")
    public ResponseEntity<List<MovieAverageRating>> getTop10Rating() {
        List<MovieAverageRating> topAverageRatings = movieRatingService.getTop10TopRatedMovies();
        return ResponseEntity.ok(topAverageRatings);
    }
}
