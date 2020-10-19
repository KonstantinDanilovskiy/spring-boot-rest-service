package com.example.javatestproject.service;

import com.example.javatestproject.dao.MovieDAO;
import com.example.javatestproject.dto.MovieDTO;
import com.example.javatestproject.dto.MovieDTOWithMetaData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class MovieService {
    private final MovieDAO movieDAO;
    private final List<Double> votesByPageList = new CopyOnWriteArrayList<>();
    private final AtomicInteger currentPage = new AtomicInteger(0);
    private int genreId;
    private final AtomicLong totalMoviesCount = new AtomicLong(0);

    public MovieService(MovieDAO movieDAO) {
        this.movieDAO = movieDAO;
    }

    @Async("averageVoteExecutor")
    public Future<Double> getAverageVoteByGenreId(int id) {
        resetCurrentPage();
        resetAverageVotes();
        resetTotalMoviesCount();
        genreId = id;
        Optional<MovieDTOWithMetaData> movieDTOWithPageInfo;
        do {
            if (Thread.currentThread().isInterrupted()) {
                log.warn("Thread {} is interrupted", Thread.currentThread().getName());
                return new AsyncResult<>(getAverageVoteResult());
            }
            currentPage.incrementAndGet();
            log.info("Get movies by genre id {} on {} page", id, currentPage);
            movieDTOWithPageInfo = movieDAO.getMovies(currentPage.get());
            votesByPageList.add(getTotalVoteByPage(id, movieDTOWithPageInfo));
            totalMoviesCount.addAndGet(getTotalMoviesCount(id, movieDTOWithPageInfo));
        } while (currentPage.get() < movieDTOWithPageInfo.map(MovieDTOWithMetaData::getTotalPages).orElse(0));
        Double result = getAverageVoteResult();
        resetCurrentPage();
        resetAverageVotes();
        resetTotalMoviesCount();
        return new AsyncResult<>(result);
    }

    private Double getTotalVoteByPage(int id, Optional<MovieDTOWithMetaData> movieDTOWithPageInfo) {
        return movieDTOWithPageInfo.map(page -> page.getMovies()
                                                    .stream()
                                                    .filter(m -> m.getGenreIds().contains(id))
                                                    .mapToDouble(MovieDTO::getVoteAverage)
                                                    .sum())
                                   .orElse(Double.NaN);
    }

    private long getTotalMoviesCount(int id, Optional<MovieDTOWithMetaData> movieDTOWithPageInfo) {
        return movieDTOWithPageInfo.map(page -> page.getMovies()
                                                    .stream()
                                                    .filter(m -> m.getGenreIds().contains(id))
                                                    .count())
                                   .orElse(0L);
    }

    public String getCurrentStatus() {
        return getAverageVoteResult().isNaN()
                ? String.format("The average vote by genre id %d is not calculated", genreId)
                : String.format("The average vote by genre id %d is %.3f based on %d pages",
                genreId, getAverageVoteResult(), currentPage.get());
    }

    private void resetCurrentPage() {
        currentPage.set(0);
    }

    private void resetTotalMoviesCount() {
        totalMoviesCount.set(0);
    }

    private void resetAverageVotes() {
        votesByPageList.clear();
    }

    public Double getAverageVoteResult() {
        double result = votesByPageList.stream()
                                       .filter(averageVote -> !Double.isNaN(averageVote))
                                       .mapToDouble(Double::doubleValue)
                                       .sum();
        return result == 0 || totalMoviesCount.get() == 0 ? Double.NaN : result / totalMoviesCount.get();
    }
}
