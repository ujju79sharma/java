FROM openjdk:15-slim
ADD build/libs/UsersEventsSync-0.0.1-SNAPSHOT.jar userseventsync-docker.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","userseventsync-docker.jar"]