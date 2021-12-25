# ttn-gateway-collector
Collects data from TTN (v3) gateways for LoRaWAN traffic analysis

Through the configuration file, you can specify the gateways you want to collect and log data from.
Optionally you can let it forward the data as semtech UDP protocol.

Example configuration file:
~~~~
---
url: "https://eu1.cloud.thethings.network/api/v3/events"
gateways:
- gatewayId: "technolution-gouda"
  apiKey: "NNSXS.3ITPM54KRJHDX6RXCCKZXFTXA4WFVIOMCTDVANY.secret"
- gatewayId: "bertrik-ttig-2"
  apiKey: "NNSXS.UZHV7MYRN3R74XXY2SM3R67CEPIYX5J3AEIGGMI.secret"
udp:
  host: example.com
  port: 1700
~~~~


