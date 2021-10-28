package nl.bertriksikken.udp;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class UdpProtocolConfig {

    @JsonProperty("host")
    String host = "";
    
    @JsonProperty("port")
    int port = 1700;
    
}
