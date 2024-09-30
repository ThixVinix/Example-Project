package com.example.exampleproject.dto.request;

import com.example.exampleproject.configs.annotations.ValidDateRange;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@ValidDateRange(dateAField = "dataInicial", dateBField = "dataFinal")
public class TestPostRequest {

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataInicial;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataFinal;

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
    
    @NotBlank(message = "{field.phoneNumber.required}")
    @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}", message = "{field.phoneNumber.invalidFormat}")
    private String telefone;

    @Size(min = 3, max = 30, message = "{field.username.size}")
    private String username;

    @Future(message = "{field.expiryDate.future}")
    @NotNull(message = "{field.expiryDate.required}")
    private LocalDate dataDeValidade;

    @DecimalMin(value = "0.0", inclusive = false, message = "{field.price.min}")
    @Digits(integer = 5, fraction = 2, message = "{field.price.invalidFormat}")
    private BigDecimal preco;
    
}
