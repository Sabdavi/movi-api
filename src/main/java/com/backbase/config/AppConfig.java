package com.backbase.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService omdbExecutor(@Value("${omdb.executor.poolSize:5}") int poolSize) {
        return Executors.newFixedThreadPool(poolSize);
    }

    @Bean
    public WebClient webClient(@Value("${omdb.host}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return jacksonObjectMapperBuilder -> {
            JavaTimeModule module = new JavaTimeModule();
            module.addSerializer(Instant.class, new CustomInstantSerializer());
            jacksonObjectMapperBuilder.modules(module);
            jacksonObjectMapperBuilder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }
}
