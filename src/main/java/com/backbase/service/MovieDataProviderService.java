package com.backbase.service;

import com.backbase.dto.OmdbMovieResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class MovieDataProviderService {
    private static final Logger logger = LogManager.getLogger(MovieDataProviderService.class);
    private final RestTemplate restTemplate;

    @Value("${omdb.api.key}")
    private String apiKey;
    @Value("${omdb.host}")
    private String host;
    @Value("${omdb.max.retries}")
    private int maxRetries;
    @Value("${omdb.delay}")
    private long delayMillis;

    public MovieDataProviderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public long getMovieBoxOffice(String title) {
        URI uri = UriComponentsBuilder.fromHttpUrl(host)
                .queryParam("t", title)
                .queryParam("apikey", apiKey)
                .build()
                .toUri();

        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                logger.info("Calling BoxOffice API for {}", title);
                OmdbMovieResponse omdbMovieResponse = restTemplate.getForObject(uri, OmdbMovieResponse.class);
                return parseBoxOffice(omdbMovieResponse);
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

    private long parseBoxOffice(OmdbMovieResponse response) {
        if (response == null ||
                "N/A".equalsIgnoreCase(response.getBoxOffice())) return 0;
        String digits = response.getBoxOffice().replaceAll("\\D", "");
        return digits.isBlank() ? 0 : Long.parseLong(digits);
    }
}
