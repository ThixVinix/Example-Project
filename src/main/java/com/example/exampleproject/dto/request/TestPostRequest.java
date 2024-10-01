package com.example.exampleproject.dto.request;

import com.example.exampleproject.configs.annotations.ValidDateRange;
import com.example.exampleproject.configs.deserializers.CustomLocalDateDeserializer;
import com.example.exampleproject.configs.deserializers.CustomLocalDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ValidDateRange(dateAField = "dataInicial", dateBField = "dataFinal")
public record TestPostRequest(

        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        LocalDateTime dataInicial,

        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        LocalDateTime dataFinal,

        @NotBlank(message = "{field.name.required}")
        String nome,

        @NotNull(message = "{field.birthdate.required}")
        @Past(message = "{field.birthdate.past}")
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        LocalDate dataNascimento,

        @NotBlank(message = "{field.email.required}")
        @Email
        String email,

        @NotBlank(message = "{field.cpf.required}")
        @CPF(message = "{field.cpf.invalid}")
        @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "{field.cpf.invalidFormat}")
        String cpf,

        @NotNull(message = "{field.age.required}")
        @Min(value = 0, message = "{field.age.minimum}")
        @Max(value = Byte.MAX_VALUE, message = "{field.age.maximum}")
        Byte idade,

        @NotBlank(message = "{field.base64Image.required}")
        String imagemBase64,

        @NotBlank(message = "{field.phoneNumber.required}")
        @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}", message = "{field.phoneNumber.invalidFormat}")
        String telefone,

        @Size(min = 3, max = 30, message = "{field.username.size}")
        String username,

        @Future(message = "{field.expiryDate.future}")
        @NotNull(message = "{field.expiryDate.required}")
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        LocalDate dataDeValidade,

        @DecimalMin(value = "0.0", inclusive = false, message = "{field.price.min}")
        @Digits(integer = 5, fraction = 2, message = "{field.price.invalidFormat}")
        BigDecimal preco
) {
}