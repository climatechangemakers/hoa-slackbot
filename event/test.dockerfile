FROM gradle:7.4.2-jdk11 AS build
RUN mkdir -p /app
COPY ./ /app
WORKDIR /app
RUN gradle --no-daemon :event:build

FROM openjdk:11.0.12-jre
WORKDIR /app
COPY --from=build /app/event/build/libs/event-0.0.1.jar /usr/local/bin
COPY event/rie-entry-script.sh /entry_script.sh
ADD aws/aws-lambda-rie /usr/local/bin/aws-lambda-rie

ENTRYPOINT [ "/entry_script.sh" ]