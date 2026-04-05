# Stage 1: Build the application process
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app

# Copy maven wrapper and pom.xml first to cache dependencies
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make the wrapper executable
RUN chmod +x ./mvnw

# Download dependencies offline (cache layer)
RUN ./mvnw dependency:go-offline || true

# Copy source code and build the application
COPY src src
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the production runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built jar file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port (Spring Boot default is 8080)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
