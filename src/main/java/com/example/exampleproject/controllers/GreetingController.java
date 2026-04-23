package com.example.exampleproject.controllers;

import com.example.exampleproject.dto.request.SearchGreetingRequest;
import com.example.exampleproject.dto.request.TestPostRequest;
import com.example.exampleproject.dto.response.TestPostResponse;
import com.example.exampleproject.enums.StatusEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springdoc.core.annotations.ParameterObject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Slf4j
@Tag(name = "Greeting", description = "Endpoints for managing greetings")
@Validated
@RestController
public class GreetingController {

    @Operation(
            operationId = "searchGreeting",
            summary = "Searches for a greeting",
            description = "This endpoint fetches a personalized greeting based on the filters provided by the user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful search",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TestPostResponse.class))})
    })
    @GetMapping("/search")
    public TestPostResponse searchGreeting(
            @Parameter(description = "Name of the user", example = "John")
            @RequestHeader(value = "nome")
            String nome,

            @ParameterObject
            @Valid
            SearchGreetingRequest request) {
        log.info("Request: {}", request);
        return TestPostResponse.builder()
                .date(new Date())
                .localDateTime(LocalDateTime.now())
                .localDate(LocalDate.now())
                .zonedDateTime(ZonedDateTime.now())
                .localTime(LocalTime.now())
                .statusEnum(StatusEnum.ACTIVE.getValue())
                .build();
    }

    @Operation(
            operationId = "createGreeting",
            summary = "Creates a new greeting",
            description = "This endpoint allows the creation of a new greeting based on the provided data."
    )
    @ApiResponse(
            responseCode = "200", description = "Greeting successfully created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TestPostResponse.class))
    )
    @PostMapping("/create/{code}")
    public TestPostResponse createGreeting(
            @Parameter(description = "Unique code for the greeting", example = "123ABC", required = true)
            @PathVariable("code")
            String code,

            @Parameter(description = "Payload for creating a new greeting", required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TestPostRequest.class)))
            @RequestBody
            @Valid
            TestPostRequest request) {

        StatusEnum statusEnum = StatusEnum.fromValueOrThrow(request.statusValueEnum());

        return TestPostResponse.builder()
                .date(new Date())
                .localDateTime(LocalDateTime.now())
                .localDate(LocalDate.now())
                .zonedDateTime(ZonedDateTime.now())
                .localTime(LocalTime.now())
                .statusEnum(statusEnum.getValue())
                .bigDecimalValue(new BigDecimal("123456789.45"))
                .doubleValue(Double.MAX_VALUE)
                .floatValue(Float.MAX_VALUE)
                .integerValue(Integer.MAX_VALUE)
                .longValue(Long.MAX_VALUE)
                .build();
    }

    @Operation(
            operationId = "updateGreeting",
            summary = "Updates an existing greeting",
            description = "This endpoint allows the full update of an existing greeting."
    )
    @ApiResponse(
            responseCode = "200", description = "Greeting successfully updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = String.class))
    )
    @PutMapping("/update")
    public String updateGreeting(
            @Parameter(description = "Name associated with the greeting", example = "John", required = true)
            @RequestParam("name")
            String name,

            @Parameter(description = "Updated details of the greeting", required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TestPostRequest.class)))
            @RequestBody
            @Valid
            TestPostRequest request) {
        return "Atualização realizada com sucesso.";
    }

    @Operation(
            operationId = "deleteGreeting",
            summary = "Deletes an existing greeting",
            description = "This endpoint deletes an existing greeting based on the provided ID."
    )
    @ApiResponse(
            responseCode = "200", description = "Greeting successfully deleted",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = String.class))
    )
    @DeleteMapping("/delete/{id}")
    public String deleteGreeting(
            @Parameter(description = "Unique identifier of the greeting to be deleted", example = "1001",
                    required = true)
            @PathVariable("id")
            Long id) {
        return "Registro deletado com sucesso.";
    }

    @Operation(
            operationId = "partialUpdateGreeting",
            summary = "Partially updates an existing greeting",
            description = "This endpoint allows partial updates to an existing greeting's details."
    )
    @ApiResponse(
            responseCode = "200", description = "Greeting successfully updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = String.class))
    )
    @PatchMapping("/partial-update/{id}")
    public String partialUpdateGreeting(
            @Parameter(description = "Unique identifier of the greeting to be partially updated",
                    example = "123",
                    required = true)
            @PathVariable("id")
            Long id,

            @Parameter(description = "Set of fields and their values to be updated in the greeting", required = true)
            @RequestBody
            Map<String, Object> updates) {
        return "Atualização parcial realizada com sucesso.";
    }

}
