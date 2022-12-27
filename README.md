# Simple Reverse Proxy using Spring Boot

## Description

This is a lightweight reverse proxy solution implemented in Java using Spring Boot 3.0. It is governed by Convention over Configuration and supports a declarative method of defining services, by using `application-prod.yml`.
The routing is done based on the `Host` header, and by default host validation is done, besides downstream and upstream logging at request level. Simple metrics are implemented such as request time.
All errors are handled using a cross-cutting aspect.
Load balancing is enabled by default using a stochastic algorithm, but an opt-in Round Robin Load Balancer is also available by setting the `proxy.services.lbPolicy` to `ROUND_ROBIN`. To improve availability and resilience, the app implements TCP health checks by polling the upstream service at the `GET /healthCheck` endpoint. By default, each request doesn't timeout and as such retry isn't attempted. This can be configured per service using `proxy.services.timeout` and `proxy.services.retries`. Multiple upstream hosts can be registered for every service, and a mix of secure and insecure connections can be used.

## System Requirements

- Java SE 17.x or greater
- Maven
- Docker
- Kubernetes
- Helm

## Building the images

- Clone the repository
- Change directory to `reverse_proxy`
- Run `mvn package`
- Run `docker build -t reverse_proxy .`
- Change directory to `express-server`
- Run `docker build -t express_server .`

## Running the application using Kubernetes

- Change directory to the project root
- Create the k8 namespace `development` if not created already (`kubectl create namespace development`)
- Run `helm install chart1 -n development chart`

## Running the application using Docker compose

- Change directory to the project root
- Spin up the containers by running `docker compose up -d`

## Testing

The `POST /echo` is implemented in the mock express server which echoes the request body. Port `8080` has been exposed when using both k8 and docker-compose, and as such it can be used to issue HTTP calls.\
e.g. <http://localhost:8080/echo>, having `Host` header equal to `myhost.com` and any request body can be provided

## Configuration

The format for the declarative yml file is the following, having values in parenthesis as default values:

```
proxy:
  listen:
    address: <proxy_address>
    port: <proxy_port>
  services:
    - name: <service_name>
      domain: <service_domain>
      lbPolicy: <RANDOM | ROUND_ROBIN> (RANDOM)
      retries: <0 -> 5> (0)
      timeout: <0 -> 60000> (0)
      tcpHealthCheck: <true | false> (false)
      hosts:
        - address: <upstream_address1>
          port: <upstream_port1>
          secure: <true | false> (false)
        - address: <upstream_address2>
          port: <upstream_port2>
          secure: <true | false> (false)
```