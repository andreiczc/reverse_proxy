FROM openjdk:17.0.1-jdk-slim

LABEL title="reverse_proxy_app" \
  description="Simple Reverse Proxy written in Java"

ARG VERSION=0.0.1

RUN mkdir -p /usr/app
WORKDIR /usr/app
ADD target/reverse-proxy-${VERSION}-SNAPSHOT.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "/usr/app/reverse-proxy-0.0.1-SNAPSHOT.jar"]