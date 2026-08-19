FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/java-app-1.0.0.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]