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
@Schema(description = "Gênero de filme")
public class GenreDto {

    @JsonProperty("id")
    @Schema(description = "Identificador do gênero")
    private Integer id;

    @JsonProperty("nome")
    @Schema(description = "Nome do gênero")
    private String name;
}
