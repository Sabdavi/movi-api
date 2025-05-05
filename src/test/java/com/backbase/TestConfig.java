package com.backbase;

import com.backbase.service.MovieDataProviderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

@EnableRetry
@Configuration
public class TestConfig {
    @Bean
    public MovieDataProviderService movieDataProviderService(WebClient webClient) {
        MovieDataProviderService service = new MovieDataProviderService(webClient, 100, 1);
        ReflectionTestUtils.setField(service, "apiKey", "dummy-key");
        ReflectionTestUtils.setField(service, "host", "http://dummy");
        return service;
    }
}
