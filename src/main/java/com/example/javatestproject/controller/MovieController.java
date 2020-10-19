package com.example.javatestproject.controller;

import com.example.javatestproject.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies")
public class MovieController {
    private static final String TASK_HAS_BEEN_STOPPED = "Task has been stopped";
    private static final String TASK_COULD_NOT_BE_CANCELLED
            = "Task has already completed, has already been cancelled, or could not be cancelled";
    private static final String NOTHING_TO_CANCEL = "Nothing to cancel";
    private Future<Double> averageVotesFuture;
    private final MovieService movieService;

    @SneakyThrows
    @GetMapping("/average-vote")
    public ResponseEntity<Double> getAverageVoteByGenreId(@RequestParam("id") int id) {
        log.info("Get average vote by genre id {}", id);
        averageVotesFuture = movieService.getAverageVoteByGenreId(id);
        Double result;
        try {
            result = averageVotesFuture.get();
        } catch (CancellationException e) {
            log.info(e.getClass().getName());
            result = movieService.getAverageVoteResult();
        }
        return result.isNaN() ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/stop")
    public String stopVoteCalculation() {
        log.info("Stop the movies vote calculation is called");
        if (Objects.nonNull(averageVotesFuture)) {
            return averageVotesFuture.cancel(true)
                    ? TASK_HAS_BEEN_STOPPED
                    : TASK_COULD_NOT_BE_CANCELLED;
        }
        return NOTHING_TO_CANCEL;
    }

    @GetMapping("/vote-calculation-status")
    public String getCurrentStatus() {
        log.info("Check the status of movies vote calculation");
        return movieService.getCurrentStatus();
    }
}
