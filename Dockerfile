####################################### Build stage #######################################
FROM maven:3.9-eclipse-temurin-21-alpine AS build-stage

ARG PROFILE

COPY pom.xml /build/
COPY rda-madmp /build/rda-madmp/
COPY rdamadmp-web /build/rdamadmp-web/
COPY settings.xml /root/.m2/settings.xml
RUN rm -f /build/rdamadmp-web/src/main/resources/config/app-*.env
RUN rm -f /build/rdamadmp-web/src/main/resources/logging/logback-*.xml

WORKDIR /build/
RUN mvn dependency:go-offline
RUN mvn clean package
######################################## Run Stage ########################################
FROM eclipse-temurin:21

ARG PROFILE
ENV SERVER_PORT=8080
EXPOSE ${SERVER_PORT}

COPY --from=build-stage /build/rdamadmp-web/target/rdamadmp-web-1.0.0-SNAPSHOT.jar /app/rdamadmp-web.jar

ENTRYPOINT ["java","-Dspring.config.additional-location=file:/config/","-Dspring.profiles.active=${PROFILE}","-Djava.security.egd=file:/dev/./urandom","-jar","/app/rdamadmp-web.jar"]
