package com.example.exampleproject.services;

import com.example.exampleproject.clients.models.Address;

public interface ZipCodeService {

    Address searchAddressByZipCode(String zipCode);
}
