package com.example.exampleproject.services.impl;

import com.example.exampleproject.clients.MovieClient;
import com.example.exampleproject.clients.models.movies.GenreListResponse;
import com.example.exampleproject.clients.models.movies.MovieDetailsResponse;
import com.example.exampleproject.clients.models.movies.MoviePagedResponse;
import com.example.exampleproject.dto.response.movies.GenreListDto;
import com.example.exampleproject.dto.response.movies.MovieDetailsDto;
import com.example.exampleproject.dto.response.movies.MoviePagedDto;
import com.example.exampleproject.mappers.MovieMapper;
import com.example.exampleproject.services.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieClient movieClient;
    private final MovieMapper movieMapper;

    @Override
    public GenreListDto listGenres() {
        GenreListResponse response = movieClient.listGenres();
        return movieMapper.toGenreListDto(response);
    }

    @Override
    public MoviePagedDto discoverByGenre(String genreId, int page) {
        MoviePagedResponse response = movieClient.discoverByGenre(genreId, page);
        return movieMapper.toMoviePagedDto(response);
    }

    @Override
    public MoviePagedDto searchByName(String query, int page) {
        MoviePagedResponse response = movieClient.searchByName(query, page);
        return movieMapper.toMoviePagedDto(response);
    }

    @Override
    public MovieDetailsDto getDetails(String movieId) {
        MovieDetailsResponse response = movieClient.getDetails(movieId);
        return movieMapper.toMovieDetailsDto(response);
    }

}
