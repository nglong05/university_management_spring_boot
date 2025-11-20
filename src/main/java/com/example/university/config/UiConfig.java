package com.example.university.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class UiConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, @Value("${app.ui.api-base:http://localhost:8080}") String apiBase) {
        return builder.rootUri(apiBase).build();
    }
}
