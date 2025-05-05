package com.backbase.service;

import com.backbase.repository.MovieAwardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class MovieAwardServiceTest {

    private MovieAwardRepository repository;
    private MovieAwardService service;

    @BeforeEach
    void setUp() {
        repository = mock(MovieAwardRepository.class);
        service = new MovieAwardService(repository);
    }

    @Test
    void shouldReturnTrueIfMovieWonBestPicture() {
        when(repository.existsByTitle("The Godfather")).thenReturn(true);

        boolean result = service.wonBestPicture("The Godfather");

        assertTrue(result);
        verify(repository).existsByTitle("The Godfather");
    }

    @Test
    void shouldReturnFalseIfMovieDidNotWinBestPicture() {
        when(repository.existsByTitle("Interstellar")).thenReturn(false);

        boolean result = service.wonBestPicture("Interstellar");

        assertFalse(result);
        verify(repository).existsByTitle("Interstellar");
    }
}
