package com.github.searcher.dto;

import java.util.List;

public class SearchResponse {
    private String message;
    private List<?> repositories;

    public SearchResponse(String message, List<?> repositories) {
        this.message = message;
        this.repositories = repositories;
    }

    public String getMessage() { return message; }
    public List<?> getRepositories() { return repositories; }
}