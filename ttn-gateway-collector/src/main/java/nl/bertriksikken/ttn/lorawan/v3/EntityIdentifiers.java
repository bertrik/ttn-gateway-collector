package nl.bertriksikken.ttn.lorawan.v3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * https://www.thethingsindustries.com/docs/api/reference/grpc/events/#message:EntityIdentifiers
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class EntityIdentifiers {

    @JsonProperty("gateway_ids")
    private GatewayIdentifiers gatewayIds;

    public static EntityIdentifiers createGatewayIds(String id) {
        EntityIdentifiers identifiers = new EntityIdentifiers();
        identifiers.gatewayIds = GatewayIdentifiers.create(id, null);
        return identifiers;
    }

    public GatewayIdentifiers getGatewayIds() {
        return gatewayIds;
    }

    /**
     * https://www.thethingsindustries.com/docs/api/reference/grpc/events/#message:GatewayIdentifiers
     */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GatewayIdentifiers(@JsonProperty("gateway_id") String gatewayId,
                                     @JsonProperty("eui") String eui) {
        public static GatewayIdentifiers create(String id, String eui) {
            return new GatewayIdentifiers(id, eui);
        }
    }
}
