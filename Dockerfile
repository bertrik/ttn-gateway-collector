# Alpine Linux with OpenJDK JRE
FROM openjdk:8-jre-alpine
MAINTAINER Bertrik Sikken bertrik@gmail.com

ADD ttn-gateway-collector/build/distributions/ttn-gateway-collector.tar /opt/

WORKDIR /opt/ttn-gateway-collector
ENTRYPOINT /opt/ttn-gateway-collector/bin/ttn-gateway-collector

