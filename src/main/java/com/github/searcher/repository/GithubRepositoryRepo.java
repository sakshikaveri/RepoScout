/**
 * Has a custom JPQL query using findWithFilters().
 * The query uses IS NULL checks so that if language or minStars is not provided, those filters are simply ignored.
 */

package com.github.searcher.repository;

import com.github.searcher.model.GithubRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface GithubRepositoryRepo extends JpaRepository<GithubRepository, Long> {

    // Used for filtering - all params are optional
    @Query("SELECT r FROM GithubRepository r WHERE " +
            "(:language IS NULL OR r.language = :language) AND " +
            "(:minStars IS NULL OR r.stars >= :minStars) " +
            "ORDER BY " +
            "CASE WHEN :sort = 'forks' THEN r.forks END DESC, " +
            "CASE WHEN :sort = 'updated' THEN r.lastUpdated END DESC, " +
            "r.stars DESC")
    List<GithubRepository> findWithFilters(
            @Param("language") String language,
            @Param("minStars") Integer minStars,
            @Param("sort") String sort
    );
}