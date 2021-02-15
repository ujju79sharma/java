FROM openjdk:15-slim
ADD build/libs/GigTurbo-0.0.1-SNAPSHOT.jar gigturbo-docker.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","gigturbo-docker.jar"]