FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/messagingService-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app.jar"]