package com.example.exampleproject.dto.response.movies;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Empresa de produção do filme")
public class ProductionCompanyDto {

    @JsonProperty("id")
    @Schema(description = "Identificador da empresa")
    private Integer id;

    @JsonProperty("caminhoLogo")
    @Schema(description = "Caminho do logo da empresa")
    private String logoPath;

    @JsonProperty("nome")
    @Schema(description = "Nome da empresa")
    private String name;

    @JsonProperty("paisOrigem")
    @Schema(description = "País de origem da empresa")
    private String originCountry;
}
