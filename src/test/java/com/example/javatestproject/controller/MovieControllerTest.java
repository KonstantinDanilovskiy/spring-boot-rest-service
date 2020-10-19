package com.example.javatestproject.controller;

import com.example.javatestproject.dao.MovieDAO;
import com.example.javatestproject.dto.MovieDTOWithMetaData;
import com.example.javatestproject.service.utils.MovieDTOWithMetaDataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WebAppConfiguration
@AutoConfigureMockMvc
class MovieControllerTest {
    private static final String GENRE_ID = "1";
    public static final String STATUS_MESSAGE = "The average vote by genre id 1 is";
    private static final String TASK_COULD_NOT_BE_CANCELLED
            = "Task has already completed, has already been cancelled, or could not be cancelled";
    private static final String TASK_HAS_BEEN_STOPPED = "Task has been stopped";
    private static final String NOTHING_TO_CANCEL = "Nothing to cancel";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private MovieDAO movieDAO;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void getAverageVoteByGenreId_whenRetrieveNull_thenReturnNotFoundStatus() throws Exception {
        when(movieDAO.getMovies((anyInt()))).thenReturn(Optional.empty());
        ResultActions resultActions = mockMvc.perform(get("/api/movies/average-vote").param("id", GENRE_ID));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void getAverageVoteByGenreId_CalculateBasedOnTwoPages() throws Exception {
        List<Optional<MovieDTOWithMetaData>> movies = MovieDTOWithMetaDataGenerator.buildTwoPagesWithMovieDTOs();
        double expectedResult = 6.95;

        when(movieDAO.getMovies(1)).thenReturn(movies.get(0));
        when(movieDAO.getMovies(2)).thenReturn(movies.get(1));
        ResultActions resultActions = mockMvc.perform(get("/api/movies/average-vote").param("id", GENRE_ID));

        resultActions.andExpect(status().isOk()).andExpect(content().json(String.valueOf(expectedResult)));
    }

    @Test
    void getCurrentStatus_whenCalculationDoesNotStart_thenReturnStatus() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/api/movies/vote-calculation-status"));

        MvcResult status = resultActions.andExpect(status().isOk()).andReturn();
        assertTrue(status.getResponse().getContentAsString().startsWith(STATUS_MESSAGE));
    }

    @Test
    void stopVoteCalculation_whenCalculationDoesNotStart_thenReturnAppropriateNotification() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/api/movies/stop"));

        resultActions.andExpect(status().isOk()).andExpect(content().string(TASK_COULD_NOT_BE_CANCELLED));
    }

    @Test
    void getAverageVoteByGenreId_whenStopCalculation_thenReturnComputedResult() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Optional<MovieDTOWithMetaData> movies = MovieDTOWithMetaDataGenerator.buildMovieDTOsWithSpecifiedTotalPages(1500);

        when(movieDAO.getMovies(anyInt())).thenReturn(movies);
        Future<ResultActions> getFuture = executorService.submit(() -> mockMvc.perform(get("/api/movies/average-vote").param("id", GENRE_ID)));
        Thread.sleep(100);
        Future<ResultActions> stopFuture = executorService.submit(() -> mockMvc.perform(post("/api/movies/stop")));
        executorService.shutdown();

        stopFuture.get().andExpect(status().isOk()).andExpect(content().string(TASK_HAS_BEEN_STOPPED));
        MvcResult getResult = getFuture.get().andExpect(status().isOk()).andReturn();
        double averageValue = Double.parseDouble(getResult.getResponse().getContentAsString());
        assertTrue(averageValue > 0);
    }
}