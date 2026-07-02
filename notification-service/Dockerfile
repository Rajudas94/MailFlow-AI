# Start from a base image that already has Java 17 installed, eclipse-temurin is the official OpenJDK distribution alpine means lightweight Linux
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container, all subsequent commands run from /app
WORKDIR /app

# copy the built .jar file (present in target folder), and rename to app.jar inside the container
COPY target/*.jar app.jar

# To run a JAR file in Java, use the terminal or command prompt command java -jar filename.jar, or in this case app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]