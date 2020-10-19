package com.example.javatestproject.service;

import com.example.javatestproject.dao.MovieDAO;
import com.example.javatestproject.dto.MovieDTOWithMetaData;
import com.example.javatestproject.service.utils.MovieDTOWithMetaDataGenerator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {MovieService.class})
class MovieServiceTest {
    @Mock
    private MovieDAO movieDAO;
    @InjectMocks
    @Autowired
    private MovieService movieService;
    @Captor
    private ArgumentCaptor<Integer> currentPage;
    private static final int GENRE_ID = 1;

    @Test
    void getAverageVoteByGenreId_requestsNumberShouldBeEqualToTotalPages() {
        int totalPages = 1500;
        Optional<MovieDTOWithMetaData> movies = MovieDTOWithMetaDataGenerator.buildMovieDTOsWithSpecifiedTotalPages(totalPages);

        when(movieDAO.getMovies(anyInt())).thenReturn(movies);
        movieService.getAverageVoteByGenreId(GENRE_ID);

        verify(movieDAO, times(totalPages)).getMovies(currentPage.capture());
        assertEquals(totalPages, currentPage.getAllValues().size());
    }

    @Test
    @SneakyThrows
    void getAverageVoteByGenreId_whenRetrieveNull_thenOnlyOneRequestAndReturnNaN() {
        when(movieDAO.getMovies(anyInt())).thenReturn(Optional.empty());
        Double expectedResult = Double.NaN;

        Future<Double> actualResult = movieService.getAverageVoteByGenreId(GENRE_ID);

        verify(movieDAO, times(1)).getMovies(currentPage.capture());
        assertEquals(currentPage.getAllValues().size(), 1);
        assertEquals(expectedResult, actualResult.get());
    }

    @Test
    @SneakyThrows
    void getAverageVoteByGenreId_whenTotalPagesAreTwo_thenCalculateAverageVoteBasedOnTwoPagesData() {
        List<Optional<MovieDTOWithMetaData>> movies = MovieDTOWithMetaDataGenerator.buildTwoPagesWithMovieDTOs();
        Double expectedResult = 6.95;

        when(movieDAO.getMovies(1)).thenReturn(movies.get(0));
        when(movieDAO.getMovies(2)).thenReturn(movies.get(1));
        Future<Double> actualResult = movieService.getAverageVoteByGenreId(GENRE_ID);

        assertEquals(expectedResult, actualResult.get());
    }

    @Test
    @SneakyThrows
    void getAverageVoteByGenreId_whenGenreIdIsNotFound_thenReturnNaN() {
        int notFoundGenreId = 100;
        Double expectedResult = Double.NaN;
        List<Optional<MovieDTOWithMetaData>> movies = MovieDTOWithMetaDataGenerator.buildTwoPagesWithMovieDTOs();

        when(movieDAO.getMovies(1)).thenReturn(movies.get(0));
        when(movieDAO.getMovies(2)).thenReturn(movies.get(1));
        Future<Double> actualResult = movieService.getAverageVoteByGenreId(notFoundGenreId);

        assertEquals(expectedResult, actualResult.get());
    }
}