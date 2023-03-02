FROM maven:latest AS dep

WORKDIR /usr/src/app
COPY ./ /usr/src/app

RUN mvn dependency:resolve

FROM dep AS build

RUN mvn install

FROM openjdk:11 AS runtime
COPY --from=build /usr/src/app/target /usr/src/app/target

CMD ["java", "-jar", "/usr/src/app/target/quarkus-app/quarkus-run.jar"]

