FROM gradle:7.4.2-jdk11 AS build
RUN mkdir -p /app
COPY ./ /app
WORKDIR /app
RUN gradle --no-daemon :webhook:build -x test

FROM openjdk:11.0.12-jre
WORKDIR /app
COPY --from=build /app/webhook/build/libs/event-0.0.1-all.jar /usr/local/bin
COPY event/run-jar.sh /usr/local/bin

ENTRYPOINT [ "/usr/local/bin/run-jar.sh" ]