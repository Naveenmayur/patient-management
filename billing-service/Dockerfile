FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml and settings for faster Maven downloads
COPY pom.xml .
COPY settings.xml /root/.m2/settings.xml

# Download dependencies using faster mirror
RUN mvn dependency:go-offline -B \
    -Dmaven.repo.local=/root/.m2/repository

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests \
    -Dmaven.repo.local=/root/.m2/repository

FROM eclipse-temurin:21-jre AS runner

WORKDIR /app

# Fixed: Correct path from builder stage
COPY --from=builder /app/target/billing-service-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8082
EXPOSE 9001

ENTRYPOINT ["java", "-jar", "app.jar"]

# To build the billing service Docker image, use the following command:
# docker build --progress=plain -t billing-service:latest .

#Stop billing service container if it's already running
# docker stop billing-service || true && docker rm billing-service || true

# To run the Docker container with grpc and REST endpoints, use the following command:
# docker run -d --name billing-service -p 8082:8082 -p 9001:9001 billing-service:latest