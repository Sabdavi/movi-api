package com.backbase.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {

    @Bean
    public ExecutorService omdbExecutor(@Value("${omdb.executor.poolSize:5}") int poolSize) {
        return Executors.newFixedThreadPool(poolSize);
    }

    @Bean
    public WebClient webClient(@Value("${omdb.host}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
