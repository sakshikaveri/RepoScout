/**
 * layer that connects the controller to the client and repository. For search: calls GithubClient then saves results.
 * For retrieval: calls the repository with filters.
 * **/

package com.github.searcher.service;

import com.github.searcher.client.GithubClient;
import com.github.searcher.model.GithubRepository;
import com.github.searcher.repository.GithubRepositoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GithubService {

    @Autowired
    private GithubRepositoryRepo githubRepo;

    @Autowired
    private GithubClient githubClient;

    public List<GithubRepository> searchAndSave(String query,
                                                String language,
                                                String sort) {
        List<GithubRepository> repos = githubClient.searchRepositories(
                query, language, sort
        );


        githubRepo.saveAll(repos);

        return repos;
    }

    public List<GithubRepository> getRepositories(String language,
                                                  Integer minStars,
                                                  String sort) {
        return githubRepo.findWithFilters(language, minStars, sort);
    }
}