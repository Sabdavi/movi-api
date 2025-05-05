Run Unit Tests:

mvn clean test

Tests cover:

CSV loading and parsing

Controller endpoints (rating, awards)

Service logic (async box office fetch, rating aggregation)

Retry, timeout, and caching behaviors

JWT authentication and error handling

Tests mock external dependencies such as OMDb API and JWT filters.

