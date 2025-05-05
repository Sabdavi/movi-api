Box office values are fetched from OMDb and cached.

If box office fetch fails, 0 is returned and used in ordering.

Users can rate multiple movies without restriction.

Title matching is case-insensitive and trimmed.

Movie titles must be valid and found on OMDb.

OMDb calls are retried on failure (3 attempts with delay).

Caching is done in-memory using Guava.