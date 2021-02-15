FROM openjdk:13
ADD build/libs/GigTurbo-all-0.0.1-SNAPSHOT.jar gigturbo-v2-docker.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","gigturbo-v2-docker.jar"]