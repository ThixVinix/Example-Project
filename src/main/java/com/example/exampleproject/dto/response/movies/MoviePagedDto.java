package com.example.exampleproject.dto.response.movies;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta paginada de filmes")
public class MoviePagedDto {

    @JsonProperty("pagina")
    @Schema(description = "Número da página atual")
    private Integer page;

    @JsonProperty("resultados")
    @Schema(description = "Lista de filmes")
    private List<MovieSummaryDto> results;

    @JsonProperty("totalPaginas")
    @Schema(description = "Total de páginas")
    private Integer totalPages;

    @JsonProperty("totalResultados")
    @Schema(description = "Total de resultados")
    private Integer totalResults;
}
