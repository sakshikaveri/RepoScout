package com.github.searcher.service;

import com.github.searcher.client.GithubClient;
import com.github.searcher.model.GithubRepository;
import com.github.searcher.repository.GithubRepositoryRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GithubServiceTest {

    @Mock
    private GithubRepositoryRepo githubRepo;

    @Mock
    private GithubClient githubClient;

    @InjectMocks
    private GithubService githubService;

    @Test
    void shouldFetchAndSaveRepositories() {
        // Fake repo returned by GitHub API
        GithubRepository fakeRepo = new GithubRepository();
        fakeRepo.setId(123L);
        fakeRepo.setName("spring-boot-example");
        fakeRepo.setStars(450);
        fakeRepo.setLanguage("Java");

        when(githubClient.searchRepositories("spring boot", "Java", "stars"))
                .thenReturn(List.of(fakeRepo));

        when(githubRepo.saveAll(anyList()))
                .thenReturn(List.of(fakeRepo));

        List<GithubRepository> result = githubService.searchAndSave(
                "spring boot", "Java", "stars"
        );

        assertEquals(1, result.size());
        assertEquals("spring-boot-example", result.get(0).getName());
        verify(githubRepo, times(1)).saveAll(anyList());
    }

    @Test
    void shouldReturnEmptyList_whenNothingSaved() {
        when(githubRepo.findWithFilters(null, null, "stars"))
                .thenReturn(List.of());

        List<GithubRepository> result = githubService.getRepositories(null, null, "stars");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowException_whenGithubApiFails() {
        when(githubClient.searchRepositories(any(), any(), any()))
                .thenThrow(new RuntimeException("GitHub API down"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> githubService.searchAndSave("spring boot", null, null));

        assertTrue(ex.getMessage().contains("GitHub API down"));
    }
}