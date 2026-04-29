FROM eclipse-temurin:21-jre AS runner

WORKDIR /app

COPY target/*.jar ./app.jar

EXPOSE 8082
EXPOSE 9001

ENTRYPOINT ["java", "-jar", "app.jar"]
