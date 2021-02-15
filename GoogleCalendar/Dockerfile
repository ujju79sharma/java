FROM openjdk:15-slim
ADD build/libs/GoogleCalendar-0.0.1-SNAPSHOT.jar googlecalendar-docker.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","googlecalendar-docker.jar"]