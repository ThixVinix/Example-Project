package com.example.exampleproject.clients.config;

import com.example.exampleproject.configs.properties.RapidApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class RapidApiWebClientConfig {

    private final RapidApiProperties rapidApiProperties;

    @Bean
    public WebClient rapidApiWebClient(ObjectProvider<WebClient.Builder> webClientBuilderProvider) {
        return webClientBuilderProvider.getObject()
                .baseUrl(rapidApiProperties.getBaseUrl())
                .defaultHeader("x-rapidapi-key", rapidApiProperties.getKey())
                .defaultHeader("x-rapidapi-host", rapidApiProperties.getHost())
                .build();
    }
}
