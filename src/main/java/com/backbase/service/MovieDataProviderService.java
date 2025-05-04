package com.backbase.service;

import com.backbase.dto.OmdbMovieResponse;
import com.backbase.exception.ExternalServiceUnavailableException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
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
                                    @Value("${cache.boxOffice.expireDays:30}") int boxOfficeCacheExpireDays ) {
        this.webClient = webClient;
        this.movieBoxOfficeCache =  CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .expireAfterWrite(boxOfficeCacheExpireDays, TimeUnit.DAYS)
                .build();
        this.movieTitleCache =  CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .build();
    }

    public long getMovieBoxOffice(String title) {

        Long cachedBoxOffice = movieBoxOfficeCache.getIfPresent(title);
        if (cachedBoxOffice != null) {
            logger.info("Returning cached value for title {}", title);
            return cachedBoxOffice;
        }

        logger.info("Calling BoxOffice API for {}", title);
        Long boxOffice =  webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("t", title)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(OmdbMovieResponse.class)
                .retryWhen(Retry.fixedDelay(maxRetries, Duration.ofMillis(delayMillis))
                        .filter(this::isRetriable))
                .map(this::parseBoxOffice)
                .doOnSuccess(parsed -> movieBoxOfficeCache.put(title, parsed))
                .onErrorResume(ex -> {
                    logger.warn("BoxOffice API failed after {} attempts: {}", maxRetries, ex.getMessage());
                    return Mono.just(0L);
                })
                .block();
        return boxOffice != null ? boxOffice : 0L;
    }

    public boolean validateMovieTitle(String title) {
        String normalizedTitle = title.trim().toLowerCase();
        Boolean cachedMovieTitle = movieTitleCache.getIfPresent(normalizedTitle);
        if (cachedMovieTitle != null) {
            logger.info("Returning cached value for title {}", title);
            return cachedMovieTitle;
        }

        logger.info("Calling title API for {}", title);
        Boolean isValid = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("t", title)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(OmdbMovieResponse.class)
                .retryWhen(Retry.fixedDelay(maxRetries, Duration.ofMillis(delayMillis))
                        .filter(this::isRetriable))
                .map(this::hasValidTitle)
                .doOnNext(valid -> movieTitleCache.put(normalizedTitle, valid))
                .onErrorResume(ex -> {
                    logger.warn("All validation attempts failed for '{}': {}", title, ex.getMessage());
                    return Mono.error(new ExternalServiceUnavailableException(
                            "Could not validate movie title at this time. Please try again later."));
                })
                .block();

        return isValid != null ? isValid : false;
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

    private boolean isRetriable(Throwable ex) {
        return ex instanceof WebClientRequestException || ex instanceof WebClientResponseException;
    }
}
