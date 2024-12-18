package com.example.exampleproject.controllers;

import com.example.exampleproject.clients.models.Address;
import com.example.exampleproject.services.ZipCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ZipCode", description = "Endpoints to manage zip code information and search addresses.")
@Validated
@RestController
public class ZipCodeController {

    private final ZipCodeService zipCodeService;

    @Autowired
    ZipCodeController(ZipCodeService zipCodeService) {
        this.zipCodeService = zipCodeService;
    }

    @Operation(
            operationId = "searchAddressByZipCode",
            summary = "Search for an address by zip code",
            description = "This endpoint retrieves address information based on the provided zip code."
    )
    @ApiResponse(
            responseCode = "200", description = "Address successfully retrieved",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Address.class))
    )
    @GetMapping("chamada/viacep/{cep}")
    public Address searchAddressByZipCode(
            @Parameter(description = "Zip code to search for the address", example = "12345-678", required = true)
            @PathVariable("cep")
            String zipCode) {
        return zipCodeService.searchAddressByZipCode(zipCode);
    }

}
