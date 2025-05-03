package com.backbase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OmdbMovieResponse {

    @JsonProperty("BoxOffice")
    private String boxOffice;

    private boolean response;

    public String getBoxOffice() {
        return boxOffice;
    }
    public void setBoxOffice(String boxOffice) {
        this.boxOffice = boxOffice;
    }

    public boolean isValidResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }
}