package com.example.javatestproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {
    private Integer id;
    @JsonProperty("vote_count")
    private Integer voteCount;
    private Boolean video;
    @JsonProperty("vote_average")
    private Double voteAverage;
    private String title;
    private Double popularity;
    @JsonProperty("original_language")
    private String originalLanguage;
    @JsonProperty("original_title")
    private String originalTitle;
    @JsonProperty("genre_ids")
    private List<Integer> genreIds;
    private Boolean adult;
    private String overview;
    @JsonDeserialize()
    @JsonProperty("release_date")
    private LocalDate releaseDate;
}
