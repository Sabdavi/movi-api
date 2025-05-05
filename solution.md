This project implements a movie awards and rating service using Spring
Boot 3, Spring Security, and a MySQL backend. Users can rate movies,
check if a movie won Best Picture, and retrieve a list of top-rated movies
based on average rating and box office value. The API also includes secure
authentication using JWT tokens and supports concurrent external API calls
with caching and retry mechanisms to enhance performance and resilience.
OMDb is used for movie metadata. The architecture separates responsibilities
into clearly defined services, controllers, and repositories,
ensuring clean design and testability.