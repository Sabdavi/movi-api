package com.backbase.controller;

import com.backbase.dto.MovieRatingRequest;
import com.backbase.entity.MovieRating;
import com.backbase.security.JwtAuthenticationFilter;
import com.backbase.security.JwtTokenProvider;
import com.backbase.service.MovieRatingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(com.backbase.controller.MovieRatingController.class)
@AutoConfigureMockMvc(addFilters = false)
class MovieRatingControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    MovieRatingService movieRatingService;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldReturnCorrectRatingResponse() throws Exception {
        MovieRatingRequest movieRatingRequest = new MovieRatingRequest("The King's Speech", 9);
        MovieRating saved = MovieRating.builder().title("The King's Speech").rate(9).build();

        when(movieRatingService.rateMovie(ArgumentMatchers.any())).thenReturn(saved);

        mockMvc.perform(post("/movies/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieRatingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The King's Speech"))
                .andExpect(jsonPath("$.rate").value(9));
    }

    @Test
    void shouldReturn400WhenRatingIsBelow1() throws Exception {
        MovieRatingRequest request = new MovieRatingRequest("Inception", 0);

        mockMvc.perform(post("/movies/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Rating must be at least 1"));
    }

    @Test
    void shouldReturn400WhenRatingIsAbove10() throws Exception {
        MovieRatingRequest request = new MovieRatingRequest("Inception", 11);

        mockMvc.perform(post("/movies/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Rating must be no more than 10"));
    }

    @Test
    void shouldReturn400WhenTitleIsBlank() throws Exception {
        MovieRatingRequest request = new MovieRatingRequest("  ", 5);
        mockMvc.perform(post("/movies/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Title must not be blank"));
    }

    @Test
    void shouldReturn400WhenTitleTooLong() throws Exception {
        String longTitle = "A".repeat(300);
        MovieRatingRequest request = new MovieRatingRequest(longTitle, 8);

        mockMvc.perform(post("/movies/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Title must be less than 255 characters"));
    }
}
