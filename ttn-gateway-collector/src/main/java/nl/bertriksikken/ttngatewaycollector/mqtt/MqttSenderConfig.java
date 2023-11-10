package nl.bertriksikken.ttngatewaycollector.mqtt;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class MqttSenderConfig {

    @JsonProperty("url")
    public String url = "tcp://stofradar.nl";

    @JsonProperty("qos")
    public int qos = 0;

    @JsonProperty("uplinkTopic")
    public String uplinkTopic = "lorawan/uplink";

    @JsonProperty("downlinkTopic")
    public String downlinkTopic = "lorawan/downlink";

}
