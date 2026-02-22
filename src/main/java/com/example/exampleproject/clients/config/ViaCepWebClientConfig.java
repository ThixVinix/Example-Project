package com.example.exampleproject.clients.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ViaCepWebClientConfig {

    @Bean
    public WebClient viaCepWebClient(ObjectProvider<WebClient.Builder> webClientBuilderProvider) {
        return webClientBuilderProvider.getObject()
                .baseUrl("https://viacep.com.br/ws")
                .build();
    }
}
