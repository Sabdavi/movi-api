
Docker Prerequisites: Ensure Docker and Docker Compose are installed.
Maven Prerequisites: Ensure Maven (3.9.9) installed.


1. Build the application Docker image:

mvn clean install docker:build

2. Start MySQL + App:

docker-compose up --build

This launches a MySQL container and a Spring Boot container (movie-api).

The application will wait for MySQL to be available using the included wait-for-mysql.sh script.

The service runs at http://localhost:8080.

Access Swagger UI at http://localhost:8080/swagger-ui.html.

API calls follow :

1. create user :
   curl --location 'http://localhost:8080/auth/users' \
   --header 'Content-Type: application/json' \
   --data '{
   "username" : "saeid",
   "password" : "*****"
   }'

2. create client :
   curl --location --request POST 'http://localhost:8080/auth/clients'

3. get token :
   curl --location 'http://localhost:8080/auth/token?username=saeid&password=abdavi' \
   --header 'Content-Type: application/json' \
   --data '{
   "clientId" : "3eb01221-b195-42b3-a918-825149ec508f",
   "clientSecret" : "ae0d8b21-14fe-497a-b671-68a60ba059a4"
   }'

4. rate some movies :
   curl --location 'http://localhost:8080/movies/rate' \
   --header 'Content-Type: application/json' \
   --header 'Authorization: Bearer *******' \
   --header 'Cookie: JSESSIONID=52140D5A134544E614E7EF8E9E294C73' \
   --data '{
   "title" : "Seven",
   "rate" : 7
   }'

5. get top rated movies :
   curl --location 'http://localhost:8080/movies/top-rated' \
   --header 'Authorization: Bearer *******' \
   --header 'Cookie: JSESSIONID=52140D5A134544E614E7EF8E9E294C73'

6. find best picture winner :
   curl --location 'http://localhost:8080/movies/won-best-picture' \
   --header 'Authorization: Bearer *******' \
   --header 'Cookie: JSESSIONID=964CFF494EEF0E1E659D19B301C3F3A7'