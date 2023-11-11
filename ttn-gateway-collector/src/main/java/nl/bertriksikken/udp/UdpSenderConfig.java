package nl.bertriksikken.udp;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class UdpSenderConfig {

    @JsonProperty("host")
    public String host = "";

    @JsonProperty("port")
    int port = 1700;

}
