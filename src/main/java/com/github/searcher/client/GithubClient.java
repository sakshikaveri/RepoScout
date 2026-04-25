package com.github.searcher.client;

import com.github.searcher.model.GithubRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class GithubClient {

    @Value("${github.token:}")  // the :  means it's optional — empty string if not set
    private String githubToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<GithubRepository> searchRepositories(String query,
                                                     String language,
                                                     String sort) {
        try {
            // Build the GitHub search query
            // e.g. "spring boot language:Java"
            String searchQuery = query;
            if (language != null && !language.isEmpty()) {
                searchQuery += " language:" + language;
            }

            // Default sort to stars if not provided
            String sortParam = (sort != null && !sort.isEmpty()) ? sort : "stars";

            String url = "https://api.github.com/search/repositories"
                    + "?q=" + searchQuery.replace(" ", "+")
                    + "&sort=" + sortParam
                    + "&per_page=30";  // fetch 30 results at a time

            // Set headers — GitHub requires Accept header
            // Adding token if available to avoid rate limits
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/vnd.github.v3+json");
            if (githubToken != null && !githubToken.isEmpty()) {
                headers.set("Authorization", "Bearer " + githubToken);
            }

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class
            );

            // Parse the response
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode items = root.get("items");

            List<GithubRepository> repos = new ArrayList<>();

            for (JsonNode item : items) {
                GithubRepository repo = new GithubRepository();
                repo.setId(item.get("id").asLong());
                repo.setName(item.get("name").asText());
                repo.setDescription(item.path("description").asText(null));
                repo.setOwnerName(item.path("owner").path("login").asText());
                repo.setLanguage(item.path("language").asText(null));
                repo.setStars(item.path("stargazers_count").asInt());
                repo.setForks(item.path("forks_count").asInt());
                repo.setHtmlUrl(item.path("html_url").asText());

                // GitHub returns date like "2024-01-01T12:00:00Z"
                String updatedAt = item.path("updated_at").asText();
                if (updatedAt != null && !updatedAt.isEmpty()) {
                    repo.setLastUpdated(LocalDateTime.parse(
                            updatedAt, DateTimeFormatter.ISO_DATE_TIME
                    ));
                }

                repos.add(repo);
            }

            return repos;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch from GitHub: " + e.getMessage());
        }
    }
}