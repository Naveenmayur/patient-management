FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy settings first for caching
COPY settings.xml ./settings.xml

# Copy pom.xml
COPY pom.xml .

# Download dependencies
RUN mvn -s settings.xml dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn -s settings.xml clean package -DskipTests

FROM eclipse-temurin:21-jre AS runner

WORKDIR /app

COPY --from=builder /app/target/eureka-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]
