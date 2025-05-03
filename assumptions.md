Assumptions

1. Box Office Retrieval Failure

If the OMDb API fails to return a box office value (due to timeout, network issues, or missing data), we assume the value to be 0. This fallback is intentional and consistent throughout the system.

This assumption directly affects sorting logic for top-rated movies:

Movies with a failed box office fetch will have boxOffice = 0

These movies will appear lower in the result list if there is a tie in average rating

We do not retry endlessly or use placeholders beyond 0

This ensures API resilience and avoids blocking behavior or incomplete responses.


2. User Rating Behavior

A user can rate multiple movies, without any restrictions on quantity or frequency.

There are no current constraints such as "only one rating per movie" or "rate only once per day".

This is to allow flexibility in testing and demonstration, and could be extended in the future to enforce stricter rules.


3. Movie Title Input

It is assumed that users provide accurate and correctly spelled movie titles when rating movies or querying data.

There is no fuzzy matching, auto-correction, or external validation performed on the title string.

Incorrect or misspelled titles may result in a failure to retrieve box office values or match ratings properly.