/**
 * It builds the GitHub API URL, sets the required HTTP headers (GitHub needs an Accept header), optionally adds the Authorization token to avoid rate limits,
 * makes the call using RestTemplate, then parses each repository item from the JSON response into a GithubRepository object.
 */

package com.github.searcher.client;

import com.github.searcher.model.GithubRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class GithubClient {

    @Value("${github.token:}")
    private String githubToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<GithubRepository> searchRepositories(String query, String language, String sort) {
        try {
            // Building the earch query, for eg- "spring boot language:Java"
            String searchQuery = query;
            String languageFilter = "";

            if (language != null && !language.isEmpty()) {
                String formattedLanguage = language.substring(0, 1).toUpperCase()
                        + language.substring(1);
                // encode only the language value to handle C++, C# etc
                try {
                    languageFilter = "+language:" + URLEncoder.encode(formattedLanguage, StandardCharsets.UTF_8);
                } catch (Exception e) {
                    languageFilter = "+language:" + formattedLanguage;
                }
            }

            // Default sorting to stars
            String sortParam;
            if (sort == null || sort.isEmpty()) {
                sortParam = "stars";
            } else {
                switch (sort.toLowerCase()) {
                    case "forks":
                        sortParam = "forks";
                        break;
                    case "updated":
                    case "lastupdated":
                        sortParam = "updated";
                        break;  // handle both
                    default:
                        sortParam = "stars";
                        break;
                }
            }

            String url = "https://api.github.com/search/repositories"
                    + "?q=" + URLEncoder.encode(searchQuery, StandardCharsets.UTF_8)
                    + languageFilter
                    + "&sort=" + sortParam
                    + "&per_page=30";

            System.out.println("GitHub URL: " + url);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/vnd.github.v3+json");
            if (githubToken != null && !githubToken.isEmpty()) {
                headers.set("Authorization", "Bearer " + githubToken);
            }
/** Because GitHub requires custom headers we can't use simple getForObject().
 * Instead we use exchange() which lets us set headers.
 * We wrap headers in HttpEntity and pass it to exchange(). **/
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // Parsing the response
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

                String updatedAt = item.path("updated_at").asText();
                if (updatedAt != null && !updatedAt.isEmpty()) {
                    repo.setLastUpdated(LocalDateTime.parse(updatedAt, DateTimeFormatter.ISO_DATE_TIME));
                }

                repos.add(repo);
            }

            return repos;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch from GitHub: " + e.getMessage());
        }
    }
}