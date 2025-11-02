# Use an official Java 21 runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the compiled .jar file into the container
# This .jar file is created by the "Build Command" on Render
COPY target/event-manager-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 so Render can connect to your app
EXPOSE 8080

# The command to run your application
ENTRYPOINT ["java", "-jar", "app.jar"]