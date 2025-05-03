package com.backbase.service;

import com.backbase.dto.MovieAverageRating;
import com.backbase.dto.MovieRatingRequest;
import com.backbase.entity.MovieRating;
import com.backbase.projection.MovieAverageRatingProjection;
import com.backbase.repository.MovieRatingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
public class MovieRatingService {
    private static final int TOP_RESULTS = 10;
    private static final Comparator<MovieAverageRating> SORT_BY_AVG_RATING_AND_BOX_OFFICE =
            Comparator.comparingDouble(MovieAverageRating::averageRate)
                    .thenComparingLong(MovieAverageRating::boxOffice);
    private final MovieRatingRepository movieRatingRepository;
    private final MovieDataProviderService movieDataProviderService;
    private final ExecutorService executorService;

    public MovieRatingService(MovieRatingRepository movieRepository, MovieDataProviderService movieDataProviderService, ExecutorService executorService) {
        this.movieRatingRepository = movieRepository;
        this.movieDataProviderService = movieDataProviderService;
        this.executorService = executorService;
    }

    public MovieRating rateMovie(MovieRatingRequest movieRatingRequest) {
        MovieRating movieRating = new MovieRating(movieRatingRequest.title(), movieRatingRequest.rate());
        return movieRatingRepository.save(movieRating);
    }

    public List<MovieAverageRating> getTop10TopRatedMovies() {
        PageRequest topTen = PageRequest.of(0, TOP_RESULTS);
        List<MovieAverageRatingProjection> averageRatingsByTitle = movieRatingRepository.findAverageRatingsByTitle(topTen);

        List<CompletableFuture<MovieAverageRating>> completableFutures = averageRatingsByTitle.stream()
                .map(projection -> CompletableFuture.supplyAsync(() -> {
                    long movieBoxOffice = movieDataProviderService.getMovieBoxOffice(projection.getTitle());
                    return new MovieAverageRating(projection.getTitle(), projection.getAverageRating(), movieBoxOffice);
                }, executorService)).toList();

        return completableFutures
                .stream()
                .map(CompletableFuture::join)
                .sorted(SORT_BY_AVG_RATING_AND_BOX_OFFICE.reversed())
                .toList();
    }

    public boolean isValidTitle(String title) {
        return movieDataProviderService.validateMovieTitle(title);
    }
}
