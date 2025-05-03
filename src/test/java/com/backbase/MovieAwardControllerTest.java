package com.backbase;

import com.backbase.security.JwtAuthenticationFilter;
import com.backbase.security.JwtTokenProvider;
import com.backbase.service.MovieAwardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest( controllers = com.backbase.controller.MovieAwardController.class)
@AutoConfigureMockMvc(addFilters = false)
class MovieAwardControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MovieAwardService movieService;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldReturnTrueWhenMovieWonBestPicture() throws Exception {
        when(jwtTokenProvider.validate(any())).thenReturn(true);
        String title = "The King's Speech";
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
