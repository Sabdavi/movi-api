package com.backbase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OmdbMovieResponse {

    @JsonProperty("BoxOffice")
    private String boxOffice;
    @JsonProperty("Response")
    private boolean response;
    @JsonProperty("Title")
    private String title;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}