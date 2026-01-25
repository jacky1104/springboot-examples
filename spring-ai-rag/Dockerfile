FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy Gradle files
COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle
COPY gradlew ./
RUN chmod +x ./gradlew

# Copy source code
COPY src ./src

# Build the application
RUN ./gradlew build -x test

# Create uploads directory
RUN mkdir -p /home/jackyxiang/codes/serverconf/FortiGateCloud/deployconf

# Copy the built JAR
RUN cp build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]