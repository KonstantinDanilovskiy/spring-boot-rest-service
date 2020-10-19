package com.example.javatestproject.service.utils;

import com.example.javatestproject.dto.MovieDTO;
import com.example.javatestproject.dto.MovieDTOWithMetaData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class MovieDTOWithMetaDataGenerator {

    public static Optional<MovieDTOWithMetaData> buildMovieDTOsWithSpecifiedTotalPages(int totalPages) {
        return Optional.of(MovieDTOWithMetaData.builder().page(1).totalPages(totalPages).movies(buildMovieDTOListForFirstPage()).build());
    }

    public static List<Optional<MovieDTOWithMetaData>> buildTwoPagesWithMovieDTOs() {
        return new ArrayList<>(Arrays.asList(
                Optional.of(MovieDTOWithMetaData.builder().page(1).totalPages(2).movies(buildMovieDTOListForFirstPage()).build()),
                Optional.of(MovieDTOWithMetaData.builder().page(2).totalPages(2).movies(builbMovieDTOListForSecondPage()).build())
        ));
    }

    private static List<MovieDTO> buildMovieDTOListForFirstPage() {
        return new ArrayList<>(Arrays.asList(
                MovieDTO.builder().genreIds(Arrays.asList(1, 2, 3)).voteAverage(7.55).build(),
                MovieDTO.builder().genreIds(Arrays.asList(2)).voteAverage(7.1).build(),
                MovieDTO.builder().genreIds(Arrays.asList(3)).voteAverage(8.1).build(),
                MovieDTO.builder().genreIds(Arrays.asList(1, 2)).voteAverage(7.7).build(),
                MovieDTO.builder().genreIds(Arrays.asList(1, 2, 5)).voteAverage(6.1).build()
        ));
    }

    private static List<MovieDTO> builbMovieDTOListForSecondPage() {
        return new ArrayList<>(Arrays.asList(
                MovieDTO.builder().genreIds(Arrays.asList(2)).voteAverage(6.5).build(),
                MovieDTO.builder().genreIds(Arrays.asList(1, 6)).voteAverage(6.0).build(),
                MovieDTO.builder().genreIds(Arrays.asList(6)).voteAverage(8.8).build(),
                MovieDTO.builder().genreIds(Arrays.asList(1)).voteAverage(7.4).build(),
                MovieDTO.builder().genreIds(Arrays.asList(2, 3)).voteAverage(7.1).build()
        ));
    }
}
