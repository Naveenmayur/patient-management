FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk AS runner

WORKDIR /app

COPY --from=builder /app/target/patient-service-0.0.1-SNAPSHOT.jar .

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "patient-service-0.0.1-SNAPSHOT.jar"]

# To build the Docker image with name, run the following command in the terminal:
# docker build -t patient-service:latest .

#Remove existing container if it exists
# docker rm -f patient-service

# To run the Docker container, use the following command:
# add env variable for billing service to connect to patient service
# SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/patient_db
# BILLING_SERVICE_HOST=billing-service
# BILLING_SERVICE_GRPC_PORT=9001
# docker run -d --name patient-service -p 8081:8081 -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/patient_db -e BILLING_SERVICE_ADDRESS=host.docker.internal -e BILLING_SERVICE_GRPC_PORT=9001 patient-service:latest