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
   curl --location 'http://localhost:8080/auth/token?username=saeid12345&password=abdavi12345' \
   --header 'Content-Type: application/json' \
   --data '{
   "clientId" : "c1323b80-9856-4864-aadd-90945521d65d",
   "clientSecret" : "a72bff04-f3b3-4d85-b3fa-934c1e1de486",
   "username" : "saeid12345",
   "password" : "*****"
   }'

4. rate some movies :
   curl --location 'http://localhost:8080/movies/rate' \
   --header 'Content-Type: application/json' \
   --header 'Authorization: Bearer *******' \
   --data '{
   "title" : "Seven",
   "rate" : 7
   }'

5. get top rated movies :
   curl --location 'http://localhost:8080/movies/top-rated' \
   --header 'Authorization: Bearer *******' \

6. find best picture winner :
   curl --location 'http://localhost:8080/movies/won-best-picture' \
   --header 'Authorization: Bearer *******' \