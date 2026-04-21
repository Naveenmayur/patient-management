FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY settings.xml ./settings.xml

COPY pom.xml .

RUN mvn -s settings.xml dependency:go-offline -B

COPY src ./src

RUN mvn -s settings.xml clean package -DskipTests

FROM eclipse-temurin:21-jre AS runner

WORKDIR /app

COPY --from=builder /app/target/billing-service-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8082
EXPOSE 9001

ENTRYPOINT ["java", "-jar", "app.jar"]
