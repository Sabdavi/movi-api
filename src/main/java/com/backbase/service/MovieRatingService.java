package com.backbase.service;

import com.backbase.dto.MovieAverageRating;
import com.backbase.dto.MovieRatingRequest;
import com.backbase.entity.MovieRating;
import com.backbase.projection.AverageRatingProjection;
import com.backbase.repository.MovieRatingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieRatingService {
    private final MovieRatingRepository movieRatingRepository;

    public MovieRatingService(MovieRatingRepository movieRepository) {
        this.movieRatingRepository = movieRepository;
    }

    public MovieRating rateMovie(MovieRatingRequest movieRatingRequest) {
        MovieRating movieRating = new MovieRating(movieRatingRequest.title(), movieRatingRequest.rate());
        return movieRatingRepository.save(movieRating);
    }

    public List<AverageRatingProjection> getTop10TopRatedMovies() {
        PageRequest topTen = PageRequest.of(0,10);
        return movieRatingRepository.findAverageRatingsByTitle(topTen);
    }

    public MovieAverageRating toDto(AverageRatingProjection movieAverageProjection) {
        return new MovieAverageRating(movieAverageProjection.getTitle(), movieAverageProjection.getAverageRating());

    }
}
