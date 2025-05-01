package com.backbase.controller;

import com.backbase.dto.MovieAwardResponse;
import com.backbase.service.MovieAwardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies")
public class MovieAwardController {

    private final MovieAwardService movieService;

    public MovieAwardController(MovieAwardService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/won-best-picture")
    public ResponseEntity<MovieAwardResponse> wonBestPicture(@RequestParam() String movieTitle) {
        boolean wonBestPicture = movieService.wonBestPicture(movieTitle);
        return ResponseEntity.ok(new MovieAwardResponse(movieTitle, wonBestPicture))  ;
    }


}
