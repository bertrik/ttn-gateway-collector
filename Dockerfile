# Alpine Linux with OpenJDK JRE
FROM adoptopenjdk/openjdk14:jre-14.0.2_12-alpine

LABEL maintainer="Bertrik Sikken bertrik@gmail.com"
LABEL org.opencontainers.image.source="https://github.com/bertrik/ttn-gateway-collector"
LABEL org.opencontainers.image.description="Reads TTN gateway event stream and sends it to log file / UDP / MQTT"
LABEL org.opencontainers.image.licenses="MIT"

ADD ttn-gateway-collector/build/distributions/ttn-gateway-collector.tar /opt/

WORKDIR /opt/ttn-gateway-collector
ENTRYPOINT ["/opt/ttn-gateway-collector/bin/ttn-gateway-collector"]

