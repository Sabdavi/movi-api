How to run : 
1. go to the project dir : cd movi-api
2. run : mvn clean install  docker:build
3. run :  docker-compose up --build


API calls follow :

1. create user :
   curl --location 'http://localhost:8080/auth/users' \
   --header 'Content-Type: application/json' \
   --data '{
   "username" : "saeid",
   "password" : "abdavi"
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

4. rete some movies :
   curl --location 'http://localhost:8080/movies/rate' \
   --header 'Content-Type: application/json' \
   --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYWVpZCIsIkNsaWVudElkIjoiM2ViMDEyMjEtYjE5NS00MmIzLWE5MTgtODI1MTQ5ZWM1MDhmIiwiaWF0IjoxNzQ2MjU2NTMxLCJleHAiOjE3NDYyNjAxMzF9.hXB91yPjdNZal7E6032RCM6B_YnmN5RFzAZ4kCB5uv4' \
   --header 'Cookie: JSESSIONID=52140D5A134544E614E7EF8E9E294C73' \
   --data '{
   "title" : "Seven",
   "rate" : 7
   }'

5. get top rated movies :
   curl --location 'http://localhost:8080/movies/top-rated' \
   --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYWVpZCIsIkNsaWVudElkIjoiM2ViMDEyMjEtYjE5NS00MmIzLWE5MTgtODI1MTQ5ZWM1MDhmIiwiaWF0IjoxNzQ2MjU2NTMxLCJleHAiOjE3NDYyNjAxMzF9.hXB91yPjdNZal7E6032RCM6B_YnmN5RFzAZ4kCB5uv4' \
   --header 'Cookie: JSESSIONID=52140D5A134544E614E7EF8E9E294C73'