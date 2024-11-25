package com.example.exampleproject.clients.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Address(
        @JsonProperty("cep") String zipCode,
        @JsonProperty("logradouro") String street,
        @JsonProperty("complemento") String complement,
        @JsonProperty("bairro") String neighborhood,
        @JsonProperty("localidade") String city,
        @JsonProperty("uf") String state,
        @JsonProperty("regiao") String region,
        @JsonProperty("unidade") String unit,
        @JsonProperty("ibge") String ibge,
        @JsonProperty("gia") String gia,
        @JsonProperty("ddd") String ddd
) { }