package com.example.exampleproject.services.impl;

import com.example.exampleproject.clients.ViaCepClient;
import com.example.exampleproject.clients.models.Address;
import com.example.exampleproject.services.ZipCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ZipCodeServiceImpl implements ZipCodeService {

    private final ViaCepClient viaCepClient;

    @Autowired
    public ZipCodeServiceImpl(ViaCepClient viaCepClient) {
        this.viaCepClient = viaCepClient;
    }

    public Address searchAddressByZipCode(String zipCode) {
        return viaCepClient.searchAddressByZipCode(zipCode);
    }

}
