# --- Stage 1: The Build Stage ---
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy the Maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Copy the source code
COPY src ./src

# --- THIS IS THE FIX ---
# Add execute permission to the Maven wrapper
RUN chmod +x ./mvnw

# Run the Maven package command to build the .jar file
RUN ./mvnw package -DskipTests


# --- Stage 2: The Run Stage ---
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy *only* the built .jar file from the 'build' stage
COPY --from=build /app/target/event-manager-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Command to run the app
ENTRYPOINT ["java", "-jar", "app.jar"]