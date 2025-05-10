package com.backbase.service;

import com.backbase.TestConfig;
import com.backbase.dto.OmdbMovieResponse;
import com.backbase.exception.ExternalServiceUnavailableException;
import com.google.common.cache.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(TestConfig.class)
class MovieDataProviderServiceTest {

    @MockBean
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Autowired
    private MovieDataProviderService movieDataProviderService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        clearCachesManually();
    }

    @Test
    void shouldRetryAndRecoverForBoxOfficeWhenWebClientFails() {

        WebClientRequestException simulatedException = new WebClientRequestException(
                new IOException("Simulated timeout"),
                HttpMethod.GET,
                URI.create("http://fake-uri"),
                new HttpHeaders()
        );

        when(responseSpec.bodyToMono(OmdbMovieResponse.class))
                .thenThrow(simulatedException)
                .thenThrow(simulatedException)
                .thenThrow(simulatedException);

        String title = "Inception";
        long result = movieDataProviderService.getMovieBoxOffice(title);
        assertEquals(0L, result, "Should fallback to 0 after retries fail");
        verify(webClient, times(3)).get();
    }

    @Test
    void shouldRetryAndRecoverForTitleWhenWebClientFails() {

        WebClientRequestException simulatedException = new WebClientRequestException(
                new IOException("Simulated timeout"),
                HttpMethod.GET,
                URI.create("http://fake-uri"),
                new HttpHeaders()
        );

        when(responseSpec.bodyToMono(OmdbMovieResponse.class))
                .thenThrow(simulatedException)
                .thenThrow(simulatedException)
                .thenThrow(simulatedException);

        String title = "Inception";
        assertThrows(ExternalServiceUnavailableException.class, () -> movieDataProviderService.validateMovieTitle(title));
        verify(webClient, times(3)).get();
    }

    @Test
    void shouldReturnBoxOfficeWhenOmdbCallIsSuccessful() {
        String title = "Inception";
        String boxOfficeString = "$123,456,789";

        OmdbMovieResponse mockResponse = mock(OmdbMovieResponse.class);
        when(mockResponse.isValid()).thenReturn(true);
        when(mockResponse.getBoxOffice()).thenReturn(boxOfficeString);

        when(responseSpec.bodyToMono(OmdbMovieResponse.class)).thenReturn(Mono.just(mockResponse));

        long actual = movieDataProviderService.getMovieBoxOffice(title);

        assertEquals(123456789L, actual);
    }

    @Test
    void shouldReturnBoxOfficeWhenOmdbCallIsNotValid() {
        String title = "Inception";
        String boxOfficeString = null;

        OmdbMovieResponse mockResponse = mock(OmdbMovieResponse.class);
        when(mockResponse.isValid()).thenReturn(false);
        when(mockResponse.getBoxOffice()).thenReturn(boxOfficeString);

        when(responseSpec.bodyToMono(OmdbMovieResponse.class)).thenReturn(Mono.just(mockResponse));

        long actual = movieDataProviderService.getMovieBoxOffice(title);

        assertEquals(0L, actual);
    }

    @Test
    void shouldReturnBoxOfficeWhenBoxOfficeIsNotAvailable() {
        String title = "Inception";
        String boxOfficeString = "N/A";
        long expectedValue = 0L;

        OmdbMovieResponse mockResponse = mock(OmdbMovieResponse.class);
        when(mockResponse.isValid()).thenReturn(true);
        when(mockResponse.getBoxOffice()).thenReturn(boxOfficeString);

        when(responseSpec.bodyToMono(OmdbMovieResponse.class)).thenReturn(Mono.just(mockResponse));

        long actual = movieDataProviderService.getMovieBoxOffice(title);

        assertEquals(expectedValue, actual);
    }

    @Test
    void shouldReturnTitleValidityWhenOmdbCallIsSuccessful() {
        String title = "Inception";

        OmdbMovieResponse mockResponse = mock(OmdbMovieResponse.class);
        when(mockResponse.isValid()).thenReturn(true);
        when(mockResponse.getTitle()).thenReturn(title);

        when(responseSpec.bodyToMono(OmdbMovieResponse.class)).thenReturn(Mono.just(mockResponse));

        boolean actual = movieDataProviderService.validateMovieTitle(title);

        assertTrue(actual);
    }

    @Test
    void shouldReturnTitleValidityWhenTitleIsNotAvailable() {
        String title = "Inception";

        OmdbMovieResponse mockResponse = mock(OmdbMovieResponse.class);
        when(mockResponse.isValid()).thenReturn(true);
        when(mockResponse.getTitle()).thenReturn("N/A");

        when(responseSpec.bodyToMono(OmdbMovieResponse.class)).thenReturn(Mono.just(mockResponse));

        boolean actual = movieDataProviderService.validateMovieTitle(title);

        assertFalse(actual);
    }

    @Test
    void shouldReturnTitleValidityWhenOmdbCallIsNotSuccessful() {
        String title = "Inception";

        OmdbMovieResponse mockResponse = mock(OmdbMovieResponse.class);
        when(mockResponse.isValid()).thenReturn(false);
        when(mockResponse.getTitle()).thenReturn(title);

        when(responseSpec.bodyToMono(OmdbMovieResponse.class)).thenReturn(Mono.just(mockResponse));

        boolean actual = movieDataProviderService.validateMovieTitle(title);

        assertFalse(actual);
    }

    @Test
    void shouldCacheBoxOfficeValue_afterFirstSuccessfulCall() {
        String title = "Interstellar";
        String boxOfficeString = "$123,000,000";
        long expected = 123000000L;

        OmdbMovieResponse mockResponse = mock(OmdbMovieResponse.class);
        when(mockResponse.isValid()).thenReturn(true);
        when(mockResponse.getBoxOffice()).thenReturn(boxOfficeString);


        when(responseSpec.bodyToMono(OmdbMovieResponse.class)).thenReturn(Mono.just(mockResponse));
        long first = movieDataProviderService.getMovieBoxOffice(title);
        assertEquals(expected, first);

        long second = movieDataProviderService.getMovieBoxOffice(title);
        assertEquals(expected, second);

        verify(webClient, times(1)).get();
    }

    @Test
    void shouldCacheTitleValidation_afterFirstSuccessfulCall() {
        String title = "The Matrix";

        OmdbMovieResponse mockResponse = mock(OmdbMovieResponse.class);
        when(mockResponse.isValid()).thenReturn(true);
        when(mockResponse.getTitle()).thenReturn(title);

        when(responseSpec.bodyToMono(OmdbMovieResponse.class)).thenReturn(Mono.just(mockResponse));

        boolean firstCall = movieDataProviderService.validateMovieTitle(title);
        assertTrue(firstCall, "First call should return true");

        boolean secondCall = movieDataProviderService.validateMovieTitle(title);
        assertTrue(secondCall, "Second call should return cached value");

        verify(webClient, times(1)).get();
    }

    void clearCachesManually() {
        Cache<String, Long> boxOfficeCache = (Cache<String, Long>)
                ReflectionTestUtils.getField(movieDataProviderService, "movieBoxOfficeCache");
        if (boxOfficeCache != null) {
            boxOfficeCache.invalidateAll();
        }

        Cache<String, Boolean> titleCache = (Cache<String, Boolean>)
                ReflectionTestUtils.getField(movieDataProviderService, "movieTitleCache");
        if (titleCache != null) {
            titleCache.invalidateAll();
        }
    }
}
