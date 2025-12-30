FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:23-jre-ubi9-minimal

WORKDIR /app
VOLUME /tmp

COPY --from=build /app/target/projektZespolowy.jar app.jar

ENV DB_CONFIG_LOCATION=file:/config/

EXPOSE 8080

ENTRYPOINT ["java","-jar", "app.jar"]
