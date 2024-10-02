package com.example.exampleproject.controllers;

import com.example.exampleproject.configs.exceptions.custom.ResourceNotFoundException;
import com.example.exampleproject.dto.request.TestPostRequest;
import com.example.exampleproject.utils.messages.MessageUtils;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
public class GreetingController {

    @GetMapping("/search")
    public String searchGreeting(@RequestParam(value = "nome")
                            String nome,
                            @RequestParam(value = "dataInicial")
                            @DateTimeFormat(pattern = "yyyy-MM-dd")
                            LocalDate dataInicial) {

        if (nome == null || nome.isEmpty()) {
            throw new ResourceNotFoundException();
        }

        return MessageUtils.getMessage("access.success", nome);
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
