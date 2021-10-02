package nl.bertriksikken.ttngatewaycollector;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class TTNGatewayCollectorConfig {
    
    @JsonProperty("url")
    String url = "https://eu1.cloud.thethings.network/api/v3/events";
    
    @JsonProperty("gateways")
    List<GatewayReceiverConfig> receivers = new ArrayList<>();
    
    TTNGatewayCollectorConfig() {
        // add one example receiver
        receivers.add(new GatewayReceiverConfig());
    }

}
