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
@Schema(description = "Lista de gêneros de filmes")
public class GenreListDto {

    @JsonProperty("generos")
    @Schema(description = "Gêneros disponíveis")
    private List<GenreDto> genres;
}
