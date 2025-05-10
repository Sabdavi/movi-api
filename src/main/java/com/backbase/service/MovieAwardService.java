package com.backbase.service;

import com.backbase.repository.MovieAwardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieAwardService {

    private final MovieAwardRepository movieAwardRepository;


    public MovieAwardService(MovieAwardRepository bestPictureWinnerRepository) {
        this.movieAwardRepository = bestPictureWinnerRepository;
    }

    @Transactional(readOnly = true)
    public boolean wonBestPicture(String movieTitle) {
        return movieAwardRepository.existsByTitle(movieTitle);
    }
}
