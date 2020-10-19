package com.example.javatestproject.dao;

import com.example.javatestproject.dto.MovieDTOWithMetaData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class MovieDAO {
    public static final String LOYALTYPLANT_URL = "https://easy.test-assignment-a.loyaltyplant.net/";
    public static final String DISCOVER_MOVIE_PATH = "/3/discover/movie";
    public static final String KEY_REQUEST_PARAM = "72b56103e43843412a992a8d64bf96e9";
    private final RestTemplate restTemplate;

    public Optional<MovieDTOWithMetaData> getMovies(int page) {
        String url = UriComponentsBuilder.fromHttpUrl(LOYALTYPLANT_URL)
                                         .path(DISCOVER_MOVIE_PATH)
                                         .queryParam("key", KEY_REQUEST_PARAM)
                                         .queryParam("page", page)
                                         .toUriString();
        log.info("Get movies from server by url {} ", url);
        ResponseEntity<MovieDTOWithMetaData> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url,
                                                    HttpMethod.GET,
                                                    null,
                                                    MovieDTOWithMetaData.class);
        } catch (RestClientException e) {
            log.warn(e.getMessage());
            return Optional.empty();
        }
        return Optional.ofNullable(responseEntity.getBody());
    }
}
