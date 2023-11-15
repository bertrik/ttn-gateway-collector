# ttn-gateway-collector
Collects data from TTN (v3) gateways for LoRaWAN traffic analysis

Through the configuration file, you can specify the gateways you want to collect and log data from.
Optionally you can let it forward the data as semtech UDP protocol.

## Configuration

### Creating a gateway API key
In order to receive data from a TTN gateway, you need an API key for the gateway.
You can create the API key in the TTN console. This API key needs to have permission 'Read gateway traffic' enabled.
You can only copy the gateway API key once, be careful to copy it in its entirety just after creating it and storing it somewhere safe.

### Configuring the collector
Configuration is stored in the ttn-gateway-collector.yaml file.
It allows you to specify one or more gateways to collect traffic from.

Additionally you can specify a host to forward the data as semtech UDP (experimental).

Example configuration file:
~~~~
---
url: "https://eu1.cloud.thethings.network/api/v3/events"
gateways:
- gatewayId: "bertrik-ttig-2"
  apiKey: "NNSXS.UZHV7MYRN3R74XXY2SM3R67CEPIYX5J3AEIGGMI.secret"
log:
  filename: "gateway.csv"
udp:
  host: "example.com"
  port: 1700
mqtt:
  url: "tcp://example.com"
  qos: 0
  uplinkTopic: "lorawan/uplink"
  downlinkTopic: "lorawan/downlink"
  statusTopic: "lorawan/status"
~~~~

## Running
To use with Docker:
* Install docker and docker-compose, for example on Debian:
  ```
  sudo apt install docker.io docker-compose
  ```
* Pull the code from github:
  ```
  git clone https://github.com/bertrik/ttn-gateway-collector
  ```
* Enter the docker directory and pull the image from the github container registry:
  ```
  cd ttn-gateway-collector
  cd docker
  docker-compose pull
  ```
* Edit the settings file with your own settings, e.g.:
  ```
  vi ttn-gateway-collector.yaml
  ```
* Run the container:
  ```
  docker-compose up
  ```
This creates a gateway.csv file which a line for each uplink packet.
Application logs are available in directory log/

