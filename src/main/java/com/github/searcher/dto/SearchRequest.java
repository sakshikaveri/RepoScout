package com.github.searcher.dto;

public class SearchRequest {
    private String query;      // e.g. "spring boot"
    private String language;   // e.g. "Java"
    private String sort;       // "stars", "forks", or "updated"

    public String getQuery() { return query; }
    public String getLanguage() { return language; }
    public String getSort() { return sort; }

    public void setQuery(String query) { this.query = query; }
    public void setLanguage(String language) { this.language = language; }
    public void setSort(String sort) { this.sort = sort; }
}