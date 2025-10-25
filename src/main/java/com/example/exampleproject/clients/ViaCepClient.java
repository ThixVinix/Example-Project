package com.example.exampleproject.clients;

import com.example.exampleproject.clients.models.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ViaCepClient {

    public static final String CEP_JSON_URI = "/{cep}/json/";

    private final WebClient viaCepWebClient;

    public Address searchAddressByZipCode(String zipCode) {
        return viaCepWebClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(CEP_JSON_URI).build(zipCode))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Address.class)
                .block();
    }
}