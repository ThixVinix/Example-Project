package com.example.exampleproject.clients;

import com.example.exampleproject.clients.models.Address;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "viacep", url = "https://viacep.com.br/ws")
public interface ViaCepClient {

    @GetMapping("/{cep}/json/")
    Address searchAddressByZipCode(@PathVariable("cep") String zipCode);
}