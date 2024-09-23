package com.example.exampleproject.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Builder
@Getter
public class TestPostRequest {

    @NotBlank(message = "{field.name.required}")
    private String nome;

    @NotNull(message = "{field.birthdate.required}")
    @Past(message = "{field.birthdate.past}")
    private LocalDate dataNascimento;

    @NotBlank(message = "{field.email.required}")
    @Email(message = "{field.email.invalidFormat}")
    private String email;

    @NotBlank(message = "{field.cpf.required}")
    @CPF(message = "{field.cpf.invalid}")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "{field.cpf.invalidFormat}")
    private String cpf;

    @NotNull(message = "{field.age.required}")
    @Min(value = 0, message = "{field.age.minimum}")
    @Max(value = Byte.MAX_VALUE, message = "{field.age.maximum}")
    private Byte idade;

    @NotBlank(message = "{field.base64Image.required}")
    private String imagemBase64;
    
}
