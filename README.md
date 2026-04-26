# GitHub Repository Searcher

## Overview
Spring Boot REST API to search GitHub repositories and store results in PostgreSQL.
Supports filtering saved results by language, minimum stars, and sort order.
Uses GitHub's repository ID as primary key — guarantees no duplicate entries (upsert on re-search).

## Tech Stack
- Java 17, Spring Boot 3.2
- Spring Data JPA + PostgreSQL
- GitHub REST API v3

## Setup

### Prerequisites
- Java 17+
- PostgreSQL running locally
- (Optional) GitHub Personal Access Token for higher rate limits

### Steps
1. Create database: `CREATE DATABASE github_db;`
2. Update `application.properties` with your PostgreSQL credentials
3. (Optional) Add your GitHub token to `application.properties`
4. Run: `mvn spring-boot:run`

## APIs

### 1. Search GitHub Repositories
POST /api/github/search

Body:
{
"query": "spring boot",
"language": "Java",
"sort": "stars"
}

### 2. Get Saved Repositories
GET /api/github/repositories?language=Java&minStars=100&sort=stars

All params optional. Default sort is stars.

## Upsert Logic
GitHub's repository ID is used as the database primary key.
Searching for the same repository twice updates the existing record
instead of creating a duplicate — handled automatically by JPA saveAll().

## Run Tests
mvn test
