package com.example.exampleproject.controllers;

import com.example.exampleproject.dto.request.TestPostRequest;
import com.example.exampleproject.dto.response.TestPostResponse;
import com.example.exampleproject.utils.DateUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Validated
@RestController
public class GreetingController {

    @GetMapping("/search")
    public TestPostResponse searchGreeting(@RequestParam(value = "nome")
                                           String name,
                                           @RequestHeader(value = "dataInicial")
                                           @DateTimeFormat(pattern = "dd/MM/yyyy")
                                           LocalDate initialDate,
                                           @RequestParam(value = "dataFinal", required = false)
                                           @DateTimeFormat(pattern = "dd/MM/yyyy")
                                           LocalDate finalDate,
                                           @RequestParam(value = "dataLocalDataTempo")
                                           @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
                                           LocalDateTime localDateTime,
                                           @RequestParam(value = "zonaDataTempo")
                                           ZonedDateTime zonedDateTime,
                                           @RequestParam(value = "tempoLocal")
                                           @DateTimeFormat(pattern = "HH:mm:ss")
                                           LocalTime localTime,
                                           @Min(value = 0)
                                           @Max(value = Byte.MAX_VALUE)
                                           @RequestParam(value = "idade")
                                           Long age,
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
                .build();
    }

    @PostMapping("/create/{code}")
    public String createGreeting(@PathVariable("code")
                                 String code,
                                 @RequestBody @Valid
                                 TestPostRequest request) {
        return "Criação realizada com sucesso.";
    }

    @PutMapping("/update")
    public String updateGreeting(@RequestParam("name") String name, @RequestBody @Valid TestPostRequest request) {
        return "Atualização realizada com sucesso.";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteGreeting(@PathVariable("id") Long id) {
        return "Registro deletado com sucesso.";
    }

    @PatchMapping("/partial-update/{id}")
    public String partialUpdateGreeting(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
        return "Atualização parcial realizada com sucesso.";
    }

}
