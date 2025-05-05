package com.backbase.controller;

import com.backbase.dto.MovieAwardResponse;
import com.backbase.service.MovieAwardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movies")
@Tag(name = "Movie Awards", description = "Endpoints for Oscar Best Picture winners")
public class MovieAwardController {

    private final MovieAwardService movieService;

    public MovieAwardController(MovieAwardService movieService) {
        this.movieService = movieService;
    }

    @Operation(summary = "Check if a movie won Best Picture")
    @GetMapping("/won-best-picture")
    public ResponseEntity<MovieAwardResponse> wonBestPicture(@RequestParam() @NotBlank(message = "movieTitle must not be blank") String movieTitle) {
        boolean wonBestPicture = movieService.wonBestPicture(movieTitle);
        return ResponseEntity.ok(new MovieAwardResponse(movieTitle, wonBestPicture));
    }


}
