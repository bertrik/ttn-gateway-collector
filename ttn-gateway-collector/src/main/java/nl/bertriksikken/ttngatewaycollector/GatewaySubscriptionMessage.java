package nl.bertriksikken.ttngatewaycollector;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

// {"identifiers":[{"gateway_ids":{"gateway_id":"technolution-gouda"}}]}
public final class GatewaySubscriptionMessage {
    
    @JsonProperty("identifiers")
    final List<Identifier> identifiers;
    
    GatewaySubscriptionMessage(String gatewayId) {
        identifiers = Arrays.asList(new Identifier(gatewayId));
    }

    private static final class Identifier {
        @JsonProperty("gateway_ids")
        GatewayIds gatewayIds = new GatewayIds();
        
        Identifier(String gatewayId) {
            this.gatewayIds.gatewayId = gatewayId;
        }
    }
    
    private static final class GatewayIds {
        @JsonProperty("gateway_id")
        String gatewayId;
    }
}
