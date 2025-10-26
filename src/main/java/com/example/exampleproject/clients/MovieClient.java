package com.example.exampleproject.clients;

import com.example.exampleproject.clients.models.movies.GenreListResponse;
import com.example.exampleproject.clients.models.movies.MovieDetailsResponse;
import com.example.exampleproject.clients.models.movies.MoviePagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class MovieClient {

    public static final MediaType APPLICATION_JSON_MEDIA_TYPE = MediaType.APPLICATION_JSON;

    private final WebClient rapidApiWebClient;

    public GenreListResponse listGenres() {
        return rapidApiWebClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/genre/movie/list").build())
                .accept(APPLICATION_JSON_MEDIA_TYPE)
                .retrieve()
                .bodyToMono(GenreListResponse.class)
                .block();
    }

    public MoviePagedResponse discoverByGenre(String genreId, int page) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("with_genres", genreId);
        params.add("page", String.valueOf(page));

        return rapidApiWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/discover/movie")
                        .queryParams(params)
                        .build())
                .accept(APPLICATION_JSON_MEDIA_TYPE)
                .retrieve()
                .bodyToMono(MoviePagedResponse.class)
                .block();
    }

    public MoviePagedResponse searchByName(String query, int page) {
        return rapidApiWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/movie")
                        .queryParam("query", query)
                        .queryParam("page", page)
                        .build())
                .accept(APPLICATION_JSON_MEDIA_TYPE)
                .retrieve()
                .bodyToMono(MoviePagedResponse.class)
                .block();
    }

    public MovieDetailsResponse getDetails(String movieId) {
        return rapidApiWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movies/getdetails")
                        .queryParam("movie_id", movieId)
                        .build())
                .accept(APPLICATION_JSON_MEDIA_TYPE)
                .retrieve()
                .bodyToMono(MovieDetailsResponse.class)
                .block();
    }
}
