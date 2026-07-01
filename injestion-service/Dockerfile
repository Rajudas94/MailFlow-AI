# Start from a base image that already has Java 17 installed
# eclipse-temurin is the official OpenJDK distribution
# alpine means lightweight Linux — reduces image size from ~500MB to ~200MB
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
# All subsequent commands run from /app
# Like doing "mkdir /app && cd /app" inside the container
WORKDIR /app

# Copy your built jar from your machine into the container
# target/*.jar matches any jar in the target folder
# app.jar is what it gets renamed to inside the container
COPY target/*.jar app.jar

# The command that runs when the container starts
# Equivalent to typing "java -jar app.jar" in your terminal
ENTRYPOINT ["java", "-jar", "app.jar"]