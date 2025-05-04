package com.backbase.service;

import com.backbase.dto.OmdbMovieResponse;
import com.backbase.exception.ExternalServiceUnavailableException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Service
public class MovieDataProviderService {
    private static final Logger logger = LogManager.getLogger(MovieDataProviderService.class);
    private static final String NON_AVAILABLE_FIELD = "N/A";
    private final RestTemplate restTemplate;

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

    public MovieDataProviderService(RestTemplate restTemplate,
                                    @Value("${cache.maxSize:5000}") int cacheSize,
                                    @Value("${cache.boxOffice.expireDays:30}") int boxOfficeCacheExpireDays ) {
        this.restTemplate = restTemplate;
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
        URI uri = generateUri(title, host, apiKey);

        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                logger.info("Calling BoxOffice API for {}", title);
                OmdbMovieResponse response = restTemplate.getForObject(uri, OmdbMovieResponse.class);
                long parsed = parseBoxOffice(response);
                movieBoxOfficeCache.put(title, parsed);
                return parsed;
            } catch (RestClientException e) {
                logger.warn("BoxOffice API call failed, attempt {} of {}", attempt + 1, maxRetries);
                attempt++;
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return 0L;
                }
            }
        }
        return 0L;
    }

    public boolean validateMovieTitle(String title) {
        String normalizedTitle = title.trim().toLowerCase();
        Boolean cachedMovieTitle = movieTitleCache.getIfPresent(normalizedTitle);
        if (cachedMovieTitle != null) {
            logger.info("Returning cached value for title {}", title);
            return cachedMovieTitle;
        }
        URI uri = generateUri(title, host, apiKey);
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                logger.info("Calling title API for {}", title);
                OmdbMovieResponse response = restTemplate.getForObject(uri, OmdbMovieResponse.class);
                if(hasValidTitle(response)) {
                    movieTitleCache.put(normalizedTitle, true);
                    return true;
                } else {
                    movieTitleCache.put(normalizedTitle, false);
                    return false;
                }
            } catch (RestClientException e) {
                logger.warn("Title API call failed, attempt {} of {}", attempt + 1, maxRetries);
                attempt++;
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new ExternalServiceUnavailableException("Could not validate movie title at this time. Please try again later.");
                }
            }
        }
        logger.warn("All title validation attempts failed for '{}'", title);
        throw new ExternalServiceUnavailableException("Could not validate movie title at this time. Please try again later.");
    }

    private URI generateUri(String title ,String host, String apiKey) {
        return UriComponentsBuilder.fromHttpUrl(host)
                .queryParam("t", title)
                .queryParam("apikey", apiKey)
                .build()
                .toUri();
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
                response.isValidResponse() &&
                hasData(response.getBoxOffice());
    }

    private boolean hasValidTitle(OmdbMovieResponse response) {
        return response != null &&
                response.isValidResponse() &&
                hasData(response.getTitle());
    }

    private boolean hasData(String field) {
        return field != null && !NON_AVAILABLE_FIELD.equalsIgnoreCase(field);
    }
}
