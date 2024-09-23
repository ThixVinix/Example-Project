package com.example.exampleproject.controllers;

import com.example.exampleproject.configs.exceptions.custom.ResourceNotFoundException;
import com.example.exampleproject.dto.request.TestPostRequest;
import com.example.exampleproject.utils.messages.MessageUtils;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Locale;

@RestController
public class GreetingController {

    @GetMapping("/greeting")
    public String greet(Locale locale) {
        return MessageUtils.getMessage("greeting", locale);
    }

    @GetMapping("/login")
    public String testLogin(Locale locale,
                            @RequestParam(value = "nome")
                            String nome,
                            @RequestParam(value = "dataInicial")
                                    @DateTimeFormat(pattern = "yyyy-MM-dd")
                            LocalDate dataInicial) {

        if (nome == null || nome.isEmpty()) {
            throw new ResourceNotFoundException(MessageUtils.getMessage("resource.not.found", locale));
        }

        return MessageUtils.getMessage("access.success", locale, nome);
    }

    @PostMapping("/test-post-mapping")
    public String testPostMapping(@RequestBody @Valid TestPostRequest request) {
        return "Deu tudo certo com o cadastro.";
    }

}
