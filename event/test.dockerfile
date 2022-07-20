FROM gradle:7.4.2-jdk11 AS build
RUN mkdir -p /app
COPY ./ /app
WORKDIR /app
RUN gradle --no-daemon :event:linkReleaseExecutableLinuxX64

FROM debian:buster-slim
WORKDIR /app
COPY --from=build /app/event/build/bin/linuxX64/releaseExecutable/event.kexe /usr/local/bin
COPY event/rie-entry-script.sh /entry_script.sh
ADD aws/aws-lambda-rie /usr/local/bin/aws-lambda-rie

ENTRYPOINT [ "/entry_script.sh" ]