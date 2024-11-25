package com.example.exampleproject.controllers;

import com.example.exampleproject.clients.models.Address;
import com.example.exampleproject.services.ZipCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class ZipCodeController {


    private final ZipCodeService zipCodeService;

    @Autowired
    ZipCodeController(ZipCodeService zipCodeService) {
        this.zipCodeService = zipCodeService;
    }

    @GetMapping("chamada/viacep/{cep}")
    public Address searchAddressByZipCode(@PathVariable("cep") String zipCode) {
        return zipCodeService.searchAddressByZipCode(zipCode);
    }

}
