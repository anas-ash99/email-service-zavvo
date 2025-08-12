FROM eclipse-temurin:21-jdk-jammy

# Optional: create a working directory
WORKDIR /app

# Copy the built JAR into the container
COPY target/email-service-0.0.1-SNAPSHOT.jar app.jar

# Expose ports
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]