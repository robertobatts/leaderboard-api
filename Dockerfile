FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} leaderboard-api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/leaderboard-api.jar"]
