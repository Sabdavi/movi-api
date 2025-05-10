FROM eclipse-temurin:17-jre
WORKDIR /app

COPY target/*.jar app.jar
COPY wait-for-mysql.sh  wait-for-mysql.sh
RUN chmod +x wait-for-mysql.sh

EXPOSE 8080
