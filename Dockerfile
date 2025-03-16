# Use Gradle with JDK 17 for building the application
FROM gradle:jdk17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy project files to the container
COPY --chown=gradle:gradle . .

# Build the Spring Boot application using Gradle
RUN gradle build -x test 

# Use a lightweight JDK 17 image for running the application
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/build/libs/Kvitter-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 10000

# Run the application
CMD ["java", "-jar", "app.jar"]