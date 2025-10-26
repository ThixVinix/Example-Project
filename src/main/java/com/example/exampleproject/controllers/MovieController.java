package com.example.exampleproject.controllers;

import com.example.exampleproject.dto.response.movies.GenreListDto;
import com.example.exampleproject.dto.response.movies.MovieDetailsDto;
import com.example.exampleproject.dto.response.movies.MoviePagedDto;
import com.example.exampleproject.services.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
@Tag(name = "Movies", description = "Endpoints para integração com RapidAPI Advanced Movie Search")
public class MovieController {

    private final MovieService movieService;

    @Operation(
            operationId = "listMovieGenres",
            summary = "Lista de gêneros de filmes",
            description = "Retorna a lista de gêneros suportados pela API de filmes."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Consulta realizada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GenreListDto.class))
            )
    })
    @GetMapping("/genres")
    public ResponseEntity<GenreListDto> listGenres() {
        return ResponseEntity.ok(movieService.listGenres());
    }

    @Operation(
            operationId = "discoverMoviesByGenre",
            summary = "Busca filmes por gênero",
            description = "Descobre filmes filtrando por um ou mais gêneros."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Consulta realizada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MoviePagedDto.class))
            )
    })
    @GetMapping("/discover")
    public ResponseEntity<MoviePagedDto> discoverByGenre(
            @Parameter(description = "IDs dos gêneros (TMDB) separados por vírgula",
                    example = "28,12", required = true)
            @RequestParam("with_genres") String genreId,
            @Parameter(description = "Número da página (padrão: 1)", example = "1")
            @RequestParam(name = "page", defaultValue = "1") int page) {
        return ResponseEntity.ok(movieService.discoverByGenre(genreId, page));
    }

    @Operation(
            operationId = "searchMoviesByName",
            summary = "Busca filmes por nome",
            description = "Pesquisa filmes pelo título informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Consulta realizada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MoviePagedDto.class))
            )
    })
    @GetMapping("/search")
    public ResponseEntity<MoviePagedDto> searchByName(
            @Parameter(description = "Termo de busca (título do filme)", example = "Inception", required = true)
            @RequestParam("query") String query,
            @Parameter(description = "Número da página (padrão: 1)", example = "1")
            @RequestParam(name = "page", defaultValue = "1") int page) {
        return ResponseEntity.ok(movieService.searchByName(query, page));
    }

    @Operation(
            operationId = "getMovieDetails",
            summary = "Detalhes de um filme",
            description = "Obtém os detalhes completos de um filme pelo seu ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Consulta realizada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MovieDetailsDto.class))
            )
    })
    @GetMapping("/{id}/details")
    public ResponseEntity<MovieDetailsDto> getDetails(
            @Parameter(description = "ID do filme", example = "550", required = true)
            @PathVariable("id") String id) {
        return ResponseEntity.ok(movieService.getDetails(id));
    }
}
