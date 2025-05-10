package com.backbase.service;

import com.backbase.dto.MovieRatingRequest;
import com.backbase.entity.MovieRating;
import com.backbase.exception.MovieNotFoundException;
import com.backbase.projection.MovieAverageRatingProjection;
import com.backbase.repository.MovieRatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieRatingServiceTest {
    private MovieRatingRepository ratingRepository;
    private MovieDataProviderService dataProviderService;
    private MovieRatingService ratingService;

    @BeforeEach
    void setUp() {
        ratingRepository = mock(MovieRatingRepository.class);
        dataProviderService = mock(MovieDataProviderService.class);
        ratingService = new MovieRatingService(ratingRepository, dataProviderService, Executors.newFixedThreadPool(5));
    }

    @Test
    void shouldSaveRatingWhenTitleIsValid() {
        MovieRatingRequest request = new MovieRatingRequest("The Matrix", 9);

        when(dataProviderService.validateMovieTitle("The Matrix")).thenReturn(true);
        when(ratingRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        MovieRating result = ratingService.rateMovie(request);

        assertNotNull(result);
        assertEquals("The Matrix", result.getTitle());
        assertEquals(9, result.getRate());

        verify(ratingRepository).save(Mockito.any());
    }

    @Test
    void shouldThrowExceptionWhenTitleIsInvalid() {
        MovieRatingRequest request = new MovieRatingRequest("Invalid Movie", 5);
        when(dataProviderService.validateMovieTitle("Invalid Movie")).thenReturn(false);

        assertThrows(MovieNotFoundException.class, () -> ratingService.rateMovie(request));

        verify(ratingRepository, never()).save(Mockito.any());
    }

    @Test
    void shouldReturnTop10SortedByRatingAndBoxOffice() {
        var projection1 = mock(MovieAverageRatingProjection.class);
        when(projection1.getTitle()).thenReturn("A");
        when(projection1.getAverageRating()).thenReturn(9.0);

        var projection2 = mock(MovieAverageRatingProjection.class);
        when(projection2.getTitle()).thenReturn("B");
        when(projection2.getAverageRating()).thenReturn(9.0);

        when(ratingRepository.findAverageRatingsByTitle(any()))
                .thenReturn(List.of(projection1, projection2));

        when(dataProviderService.getMovieBoxOffice("A")).thenReturn(100L);
        when(dataProviderService.getMovieBoxOffice("B")).thenReturn(50L);

        var result = ratingService.getTop10TopRatedMovies();

        assertEquals(2, result.size());
        assertEquals("A", result.get(0).title());
        assertEquals("B", result.get(1).title());
    }
}
