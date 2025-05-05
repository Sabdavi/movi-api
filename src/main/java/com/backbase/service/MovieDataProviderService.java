package com.backbase.service;

import com.backbase.dto.OmdbMovieResponse;
import com.backbase.exception.ExternalServiceUnavailableException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.TimeUnit;

@Service
public class MovieDataProviderService {
    private static final Logger logger = LogManager.getLogger(MovieDataProviderService.class);
    private static final String NON_AVAILABLE_FIELD = "N/A";
    private final WebClient webClient;

    private final Cache<String, Long> movieBoxOfficeCache;
    private final Cache<String, Boolean> movieTitleCache;

    @Value("${omdb.api.key}")
    private String apiKey;
    @Value("${omdb.host}")
    private String host;
    @Value("${omdb.max.retries}")
    private int maxRetries;
    @Value("${omdb.delay}")
    private long delayMillis;

    public MovieDataProviderService(WebClient webClient,
                                    @Value("${cache.maxSize:5000}") int cacheSize,
                                    @Value("${cache.boxOffice.expireDays:30}") int boxOfficeCacheExpireDays) {
        this.webClient = webClient;
        this.movieBoxOfficeCache = CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .expireAfterWrite(boxOfficeCacheExpireDays, TimeUnit.DAYS)
                .build();
        this.movieTitleCache = CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .build();
    }

    @Retryable(
            retryFor = {WebClientRequestException.class, WebClientResponseException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public long getMovieBoxOffice(String title) {

        Long cachedBoxOffice = movieBoxOfficeCache.getIfPresent(title);
        if (cachedBoxOffice != null) {
            logger.info("Returning cached value for title {}", title);
            return cachedBoxOffice;
        }

        logger.info("Calling BoxOffice API for {}", title);
        OmdbMovieResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("t", title)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(OmdbMovieResponse.class)
                .block();
        long boxOffice = parseBoxOffice(response);
        movieBoxOfficeCache.put(title, boxOffice);
        return boxOffice;
    }

    @Recover
    public long recoverBoxOfficeValidation(Exception ex, String title) {
        logger.warn("BoxOffice API call failed for '{}' after retries: {}", title, ex.getMessage());
        return 0L;
    }

    @Retryable(
            retryFor = {WebClientRequestException.class, WebClientResponseException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public boolean validateMovieTitle(String title) {
        String normalizedTitle = title.trim().toLowerCase();
        Boolean cachedMovieTitle = movieTitleCache.getIfPresent(normalizedTitle);
        if (cachedMovieTitle != null) {
            logger.info("Returning cached value for title {}", title);
            return cachedMovieTitle;
        }

        logger.info("Calling title API for {}", title);
        OmdbMovieResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("t", title)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(OmdbMovieResponse.class)
                .block();

        boolean isValid = hasValidTitle(response);
        movieTitleCache.put(normalizedTitle, isValid);
        return isValid;
    }

    @Recover
    public boolean recoverTitleValidation(Exception ex, String title) {
        logger.warn("Title validation failed for '{}' after retries: {}", title, ex.getMessage());
        throw new ExternalServiceUnavailableException("Could not validate movie title at this time. Please try again later.");
    }

    private long parseBoxOffice(OmdbMovieResponse response) {
        if (!hasValidBoxOffice(response)) {
            return 0L;
        }
        String digits = response.getBoxOffice().replaceAll("\\D", "");
        return digits.isBlank() ? 0L : Long.parseLong(digits);
    }

    private boolean hasValidBoxOffice(OmdbMovieResponse response) {
        return response != null &&
                response.isValid() &&
                hasData(response.getBoxOffice());
    }

    private boolean hasValidTitle(OmdbMovieResponse response) {
        return response != null &&
                response.isValid() &&
                hasData(response.getTitle());
    }

    private boolean hasData(String field) {
        return field != null && !NON_AVAILABLE_FIELD.equalsIgnoreCase(field);
    }
}
