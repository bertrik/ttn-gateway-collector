package nl.bertriksikken.ttn.lorawan.v3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * https://www.thethingsindustries.com/docs/reference/api/events/#message:EntityIdentifiers
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class EntityIdentifiers {

    @JsonProperty("gateway_ids")
    private GatewayIdentifiers gatewayIds;

    public GatewayIdentifiers getGatewayIds() {
        return gatewayIds;
    }

    public static EntityIdentifiers createGatewayIds(String id) {
        EntityIdentifiers identifiers = new EntityIdentifiers();
        identifiers.gatewayIds = GatewayIdentifiers.create(id, null);
        return identifiers;
    }

    /**
     * https://www.thethingsindustries.com/docs/reference/api/events/#message:GatewayIdentifiers
     */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class GatewayIdentifiers {

        @JsonProperty("gateway_id")
        public String gatewayId;
        @JsonProperty("eui")
        public String eui;

        public static GatewayIdentifiers create(String id, String eui) {
            GatewayIdentifiers identifiers = new GatewayIdentifiers();
            identifiers.gatewayId = id;
            identifiers.eui = eui;
            return identifiers;
        }
    }
}
