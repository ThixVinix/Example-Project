package com.example.exampleproject.configs;

import com.example.exampleproject.configs.properties.RapidApiProperties;
import io.micrometer.observation.ObservationRegistry;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private static final int DEFAULT_BUFFER_SIZE_BYTES = 2 * 1024 * 1024; // 2MB buffer

    private final RapidApiProperties rapidApiProperties;

    /**
     * Creates a prototype-scoped {@link WebClient.Builder} bean configured with
     * default settings such as custom codecs, memory limits, timeouts, and observation registry.
     *
     * @param observationRegistry the registry that captures observations and metrics for WebClient requests.
     * @return a configured {@link WebClient.Builder} instance.
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public WebClient.Builder webClientBuilder(ObservationRegistry observationRegistry) {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer ->
                        configurer
                                .defaultCodecs()
                                .maxInMemorySize(DEFAULT_BUFFER_SIZE_BYTES))
                .build();

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .responseTimeout(Duration.ofSeconds(5))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(5, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(5, TimeUnit.SECONDS)));

        return WebClient.builder()
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .exchangeStrategies(strategies)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .observationRegistry(observationRegistry);
    }

    @Bean
    public WebClient rapidApiWebClient(ObjectProvider<WebClient.Builder> webClientBuilderProvider) {
        return webClientBuilderProvider.getObject()
                .baseUrl(rapidApiProperties.getBaseUrl())
                .defaultHeader("x-rapidapi-key", rapidApiProperties.getKey())
                .defaultHeader("x-rapidapi-host", rapidApiProperties.getHost())
                .build();
    }

    @Bean
    public WebClient jsonPlaceholderWebClient(ObjectProvider<WebClient.Builder> webClientBuilderProvider) {
        return webClientBuilderProvider.getObject()
                .baseUrl("https://jsonplaceholder.typicode.com")
                .build();
    }

    @Bean
    public WebClient viaCepWebClient(ObjectProvider<WebClient.Builder> webClientBuilderProvider) {
        return webClientBuilderProvider.getObject()
                .baseUrl("https://viacep.com.br/ws")
                .build();
    }
}
