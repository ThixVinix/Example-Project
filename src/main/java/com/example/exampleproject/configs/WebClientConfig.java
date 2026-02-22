package com.example.exampleproject.configs;

import io.micrometer.observation.ObservationRegistry;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.Connection;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    private static final int DEFAULT_BUFFER_SIZE_BYTES = 2 * 1024 * 1024; // 2MB buffer

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
                .codecs((ClientCodecConfigurer configurer) ->
                        configurer
                                .defaultCodecs()
                                .maxInMemorySize(DEFAULT_BUFFER_SIZE_BYTES))
                .build();

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .responseTimeout(Duration.ofSeconds(5))
                .doOnConnected((Connection conn) -> conn
                        .addHandlerLast(new ReadTimeoutHandler(5, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(5, TimeUnit.SECONDS)));

        return WebClient.builder()
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .exchangeStrategies(strategies)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .observationRegistry(observationRegistry);
    }

}
