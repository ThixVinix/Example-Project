package com.example.exampleproject.services;

import com.example.exampleproject.dto.response.movies.GenreListDto;
import com.example.exampleproject.dto.response.movies.MovieDetailsDto;
import com.example.exampleproject.dto.response.movies.MoviePagedDto;

public interface MovieService {
    GenreListDto listGenres();
    MoviePagedDto discoverByGenre(String genreId, int page);
    MoviePagedDto searchByName(String query, int page);
    MovieDetailsDto getDetails(String movieId);
}
