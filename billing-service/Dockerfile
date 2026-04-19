FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk AS runner

WORKDIR /app

COPY --from=builder ./app/target/billing-service-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8082
EXPOSE 9001

ENTRYPOINT ["java", "-jar", "app.jar"]

# To build the billing service Docker image, use the following command:
# docker build -t billing-service:latest .

# To run the Docker container with grpc and REST endpoints, use the following command:
# docker run -d --name billing-service -p 8082:8082 -p 9001:9001  billing-service:latest