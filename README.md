# ttn-gateway-collector
Collects data from TTN (v3) gateways for LoRaWAN traffic analysis

Through the configuration file, you can specify the gateways you want to collect and log data from.
Optionally you can let it forward the data as semtech UDP protocol.

Example configuration file:
~~~~
---
url: "https://eu1.cloud.thethings.network/api/v3/events"
gateways:
- gatewayId: "bertrik-ttig-2"
  apiKey: "NNSXS.UZHV7MYRN3R74XXY2SM3R67CEPIYX5J3AEIGGMI.secret"
udp:
  host: example.com
  port: 1700
~~~~

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

Application logs with be available in director log/

