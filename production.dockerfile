FROM gradle:7.4.2-jdk11 AS build
RUN mkdir -p /app
COPY ./ /app
WORKDIR /app
RUN gradle --no-daemon :webhook:linkReleaseExecutableLinuxX64

FROM debian:buster-slim
WORKDIR /app
COPY --from=build /app/webhook/build/bin/linuxX64/releaseExecutable/webhook.kexe /usr/local/bin

ENTRYPOINT [ "/usr/local/bin/webhook.kexe" ]