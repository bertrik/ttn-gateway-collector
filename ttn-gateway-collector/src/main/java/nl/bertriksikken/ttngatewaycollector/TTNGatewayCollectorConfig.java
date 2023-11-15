package nl.bertriksikken.ttngatewaycollector;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bertriksikken.ttngatewaycollector.export.CsvLoggerConfig;
import nl.bertriksikken.ttngatewaycollector.mqtt.MqttSenderConfig;
import nl.bertriksikken.ttngatewaycollector.udp.UdpSenderConfig;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public final class TTNGatewayCollectorConfig {

    @JsonProperty("url")
    String url = "https://eu1.cloud.thethings.network/api/v3/events";

    @JsonProperty("gateways")
    List<GatewayReceiverConfig> receivers = new ArrayList<>();

    @JsonProperty("log")
    CsvLoggerConfig csvLoggerConfig = new CsvLoggerConfig();

    @JsonProperty("udp")
    UdpSenderConfig udpProtocolConfig = new UdpSenderConfig();

    @JsonProperty("mqtt")
    MqttSenderConfig mqttSenderConfig = new MqttSenderConfig();

    TTNGatewayCollectorConfig() {
        // add one example receiver
        receivers.add(new GatewayReceiverConfig());
    }

}
