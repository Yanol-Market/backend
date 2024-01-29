FROM openjdk:17-ea-11-jdk-slim
ARG JAR_FILE=/build/libs/*.jar
COPY ${JAR_FILE} /app.jar
ENTRYPOINT [ \
  "java", \
  "-Dspring.profiles.active=${PROFILE}", \
  "-jar", \
  "app.jar" \
]
