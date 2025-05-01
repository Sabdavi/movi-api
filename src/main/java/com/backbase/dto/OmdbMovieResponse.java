package com.backbase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OmdbMovieResponse {

    @JsonProperty("BoxOffice")
    private String boxOffice;

    public String getBoxOffice() {
        return boxOffice;
    }

    public void setBoxOffice(String boxOffice) {
        this.boxOffice = boxOffice;
    }
}