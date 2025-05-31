FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Gradle files
COPY gradle/ gradle/
COPY gradlew .
COPY gradlew.bat .
COPY settings.gradle.kts .
COPY build.gradle.kts .

# Copy source code
COPY server/ server/

# Build the application
RUN chmod +x ./gradlew && ./gradlew :server:bootJar

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built jar file
COPY --from=build /app/server/build/libs/*.jar app.jar

# Create directory for file storage
RUN mkdir -p /app/file-storage

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=dev
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/vibe
ENV LOCAL_STORAGE_PATH=/app/file-storage

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]