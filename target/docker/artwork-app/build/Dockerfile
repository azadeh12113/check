FROM openjdk:17-jdk-slim

ARG jarToCopy

COPY /target/$jarToCopy /app/app.jar

CMD ["java", "-jar", "/app/app.jar"]
