FROM maven:3.8.5-openjdk-17 as build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17
WORKDIR /app
COPY --from=build ./app/target/*.jar ./projeto.jar
ARG EUREKA_SERVER=localhost
ARG RABBITMQ_SERVER=rabbitmq-host
ENTRYPOINT java -jar -Dspring.profiles.active=prod projeto.jar