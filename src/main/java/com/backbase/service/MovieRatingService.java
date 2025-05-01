package com.backbase.service;

import com.backbase.dto.MovieAverageRating;
import com.backbase.dto.MovieRatingRequest;
import com.backbase.entity.MovieRating;
import com.backbase.projection.MovieAverageRatingProjection;
import com.backbase.repository.MovieRatingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieRatingService {
    private final MovieRatingRepository movieRatingRepository;
    private final MovieDataProviderService movieDataProviderService;

    public MovieRatingService(MovieRatingRepository movieRepository, MovieDataProviderService movieDataProviderService) {
        this.movieRatingRepository = movieRepository;
        this.movieDataProviderService = movieDataProviderService;
    }

    public MovieRating rateMovie(MovieRatingRequest movieRatingRequest) {
        MovieRating movieRating = new MovieRating(movieRatingRequest.title(), movieRatingRequest.rate());
        return movieRatingRepository.save(movieRating);
    }

    public List<MovieAverageRatingProjection> getTop10TopRatedMovies() {
        PageRequest topTen = PageRequest.of(0,10);
        return movieRatingRepository.findAverageRatingsByTitle(topTen);
    }

    public MovieAverageRating toDto(MovieAverageRatingProjection movieAverageProjection) {
        return new MovieAverageRating(movieAverageProjection.getTitle(), movieAverageProjection.getAverageRating());

    }
}
