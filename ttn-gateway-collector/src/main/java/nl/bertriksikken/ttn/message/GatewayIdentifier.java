package nl.bertriksikken.ttn.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class GatewayIdentifier {

    @JsonProperty("gateway_ids")
    public GatewayIds gatewayId = new GatewayIds();

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class GatewayIds {
        @JsonProperty("gateway_id")
        public String gatewayId = "";

        @JsonProperty("eui")
        public String eui = "";
    }
}