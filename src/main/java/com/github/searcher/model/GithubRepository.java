/**
 * Maps to github_repositories table.
 * Uses GitHub's own numeric ID as primary key — this is the key design decision that enables upsert.
 */

package com.github.searcher.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "github_repositories")
public class GithubRepository {

    @Id

    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "language")
    private String language;

    @Column(name = "stars")
    private Integer stars;

    @Column(name = "forks")
    private Integer forks;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "html_url")
    private String htmlUrl;

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getOwnerName() { return ownerName; }
    public String getLanguage() { return language; }
    public Integer getStars() { return stars; }
    public Integer getForks() { return forks; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public String getHtmlUrl() { return htmlUrl; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public void setLanguage(String language) { this.language = language; }
    public void setStars(Integer stars) { this.stars = stars; }
    public void setForks(Integer forks) { this.forks = forks; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    public void setHtmlUrl(String htmlUrl) { this.htmlUrl = htmlUrl; }
}