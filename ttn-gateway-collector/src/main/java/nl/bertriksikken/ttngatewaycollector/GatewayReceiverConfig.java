package nl.bertriksikken.ttngatewaycollector;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class GatewayReceiverConfig {
    
    @JsonProperty("gatewayId")
    String gatewayId = "";
    
    @JsonProperty ("apiKey")
    String apiKey = "";

    // jackson constructor
    GatewayReceiverConfig() {
        this("gatewayId", "apiKey");
    }
    
    GatewayReceiverConfig(String gatewayId, String apiKey) {
        this.gatewayId = gatewayId;
        this.apiKey = apiKey;
    }
    
}
