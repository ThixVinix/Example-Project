package com.example.exampleproject.controllers;

import com.example.exampleproject.dto.request.TestPostRequest;
import com.example.exampleproject.dto.response.TestPostResponse;
import com.example.exampleproject.utils.DateUtils;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@RestController
public class GreetingController {

    @GetMapping("/search")
    public TestPostResponse searchGreeting(@RequestParam(value = "nome")
                                           String name,
                                           @RequestParam(value = "dataInicial", required = false)
                                           @DateTimeFormat(pattern = "yyyy-MM-dd")
                                           LocalDate initialDate,
                                           @RequestParam(value = "dataFinal", required = false)
                                           @DateTimeFormat(pattern = "yyyy-MM-dd")
                                           LocalDate finalDate) {

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
