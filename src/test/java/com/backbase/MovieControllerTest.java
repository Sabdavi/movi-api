package com.backbase;

import com.backbase.controller.MovieController;
import com.backbase.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;


@WebMvcTest(MovieController.class)
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;


    @Test
    void shouldReturnTrueWhenMovieWonBestPicture() throws Exception {
        String title =  "The King's Speech";
        when(movieService.wonBestPicture(title)).thenReturn(true);
        mockMvc.perform(get("/movies/won-best-picture")
                        .param("movieTitle", title))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(title)))
                .andExpect(jsonPath("$.wonBestPicture", is(true)));
    }

    @Test
    void shouldReturnFalseWhenMovieDidNotWinBestPicture() throws Exception {
        String title = "Inception";
        when(movieService.wonBestPicture(title)).thenReturn(false);

        mockMvc.perform(get("/movies/won-best-picture")
                        .param("movieTitle", title))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(title)))
                .andExpect(jsonPath("$.wonBestPicture", is(false)));
    }

    @Test
    void shouldReturnBadRequestWhenParamMissing() throws Exception {
        mockMvc.perform(get("/movies/won-best-picture"))
                .andExpect(status().isBadRequest());
    }
}
