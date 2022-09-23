# Alpine Linux with OpenJDK JRE
FROM adoptopenjdk/openjdk14:jre-14.0.2_12-alpine
MAINTAINER Bertrik Sikken bertrik@gmail.com

ADD ttn-gateway-collector/build/distributions/ttn-gateway-collector.tar /opt/

WORKDIR /opt/ttn-gateway-collector
ENTRYPOINT /opt/ttn-gateway-collector/bin/ttn-gateway-collector

