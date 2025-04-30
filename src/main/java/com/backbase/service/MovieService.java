package com.backbase.service;

import com.backbase.repository.BestPictureWinnerRepository;
import org.springframework.stereotype.Service;

@Service
public class MovieService {

    private final BestPictureWinnerRepository bestPictureWinnerRepository;

    public MovieService(BestPictureWinnerRepository bestPictureWinnerRepository) {
        this.bestPictureWinnerRepository = bestPictureWinnerRepository;
    }

    public boolean wonBestPicture(String movieTitle) {
        return false;
    }
}
