package com.github.searcher.controller;

import com.github.searcher.dto.SearchRequest;
import com.github.searcher.dto.SearchResponse;
import com.github.searcher.model.GithubRepository;
import com.github.searcher.service.GithubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/github")
public class GithubController {

    @Autowired
    private GithubService githubService;

    // API 1- search GitHub and save results
    @PostMapping("/search")
    public ResponseEntity<?> searchRepositories(@RequestBody SearchRequest request) {
        try {
            if (request.getQuery() == null || request.getQuery().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Query cannot be empty"));
            }

            List<GithubRepository> repos = githubService.searchAndSave(
                    request.getQuery(),
                    request.getLanguage(),
                    request.getSort()
            );

            return ResponseEntity.ok(
                    new SearchResponse("Repositories fetched and saved successfully", repos)
            );

        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // API 2- getting saved repositories with optional filter
    @GetMapping("/repositories")
    public ResponseEntity<?> getRepositories(
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Integer minStars,
            @RequestParam(required = false, defaultValue = "stars") String sort) {

        List<GithubRepository> repos = githubService.getRepositories(
                language, minStars, sort
        );

        return ResponseEntity.ok(
                new SearchResponse("Repositories retrieved successfully", repos)
        );
    }
}