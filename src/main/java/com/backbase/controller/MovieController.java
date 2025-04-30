package com.backbase.controller;

import com.backbase.dto.MovieAwardResponse;
import com.backbase.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/won-best-picture")
    public ResponseEntity<MovieAwardResponse> wonBestPicture(@RequestParam() String movieTitle) {
        boolean wonBestPicture = movieService.wonBestPicture(movieTitle);
        return ResponseEntity.ok(new MovieAwardResponse(movieTitle, wonBestPicture))  ;
    }
}
