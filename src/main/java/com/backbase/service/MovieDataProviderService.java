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
    private static final int MAX_RETRIES = 3;
    private static final long DELAY_MILLIS = 1000;

    @Value("${omdb.api.key}")
    private String apiKey;

    @Value("${omdb.host}")
    private String host;

    private final RestTemplate restTemplate;

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
        while (attempt < MAX_RETRIES) {
            try{
                OmdbMovieResponse omdbMovieResponse = restTemplate.getForObject(uri, OmdbMovieResponse.class);
                return parseBoxOffice(omdbMovieResponse);
            } catch (RestClientException e) {
                logger.info("Fail to get boxOffice data, retrying...");
                attempt++;
                try {
                    Thread.sleep(DELAY_MILLIS);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return 0;
                }
            }
        }
        return 0;
    }

    private long parseBoxOffice(OmdbMovieResponse response) {
        //@Todo assumtion 1
        if ( response == null ||
                response.getBoxOffice() == null ||
                response.getBoxOffice().equalsIgnoreCase("N/A")) return 0;
        String digits = response.getBoxOffice().replaceAll("\\D", "");
        return digits.isBlank() ? 0 : Long.parseLong(digits);
    }
}
