package com.backbase.service;

import com.backbase.repository.MovieAwardRepository;
import org.springframework.stereotype.Service;

@Service
public class MovieAwardService {

    private final MovieAwardRepository movieAwardRepository;


    public MovieAwardService(MovieAwardRepository bestPictureWinnerRepository) {
        this.movieAwardRepository = bestPictureWinnerRepository;
    }

    public boolean wonBestPicture(String movieTitle) {
        return movieAwardRepository.existsByTitle(movieTitle);
    }
}
