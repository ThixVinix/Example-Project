package com.example.exampleproject.controllers;

import com.example.exampleproject.dto.request.TestPostRequest;
import com.example.exampleproject.dto.response.TestPostResponse;
import com.example.exampleproject.enums.StatusEnum;
import com.example.exampleproject.utils.DateUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

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
            @RequestParam(value = "nome")
            String name,

            @Parameter(description = "Initial date. Format: dd/MM/yyyy", example = "01/01/2022", required = true)
            @RequestHeader(value = "dataInicial")
            @DateTimeFormat(pattern = "dd/MM/yyyy")
            LocalDate initialDate,

            @Parameter(description = "Final date. Format: dd/MM/yyyy", example = "31/12/2022", required = true)
            @RequestParam(value = "dataFinal")
            @DateTimeFormat(pattern = "dd/MM/yyyy")
            LocalDate finalDate,

            @Parameter(description = "Local date and time. Format: dd/MM/yyyy HH:mm:ss",
                    example = "01/01/2022 00:00:00", required = true)
            @RequestParam(value = "dataLocalDataTempo")
            @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
            LocalDateTime localDateTime,

            @Parameter(description = "Date and time with timezone. Format: yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                    example = "2023-11-04T15:20:30.123555Z")
            @RequestParam(value = "zonaDataTempo", required = false)
            ZonedDateTime zonedDateTime,

            @Parameter(description = "Local time. Format: HH:mm:ss", example = "00:00:00",
                    schema = @Schema(type = "string"))
            @DateTimeFormat(pattern = "HH:mm:ss")
            @RequestParam(value = "tempoLocal", required = false)
            LocalTime localTime,

            @Parameter(description = "Age, minimum of 0 to maximum of 127", example = "65", required = true)
            @Min(value = 0)
            @Max(value = Byte.MAX_VALUE)
            @RequestParam(value = "idade")
            Long age,

            @Parameter(description = "Price, must be greater than 0.0 with up to 1 integer digit and 2 fractional " +
                    "digits. For example, 1.42 is valid.", example = "6.23", required = true)
            @DecimalMin(value = "0.0", inclusive = false)
            @Digits(integer = 1, fraction = 2)
            @RequestParam(value = "preco")
            BigDecimal price) {

        DateUtils.checkDateRange(initialDate, "dataInicial", finalDate, "dataFinal");

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
