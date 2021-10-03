package nl.bertriksikken.ttn.eventstream;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * See https://www.thethingsindustries.com/docs/reference/api/events/#message:StreamEventsRequest
 */
@JsonAutoDetect(fieldVisibility = Visibility.NONE)
public final class StreamEventsRequest {
    
    private EntityIdentifiers entityIdentifiers = new EntityIdentifiers();
    
    @JsonProperty("identifiers")
    private final List<EntityIdentifiers> identifiers = Arrays.asList(entityIdentifiers);
    
    public StreamEventsRequest(String gatewayId) {
        entityIdentifiers.gatewayIds.gatewayId = gatewayId;
    }
    
    /**
     * See https://www.thethingsindustries.com/docs/reference/api/events/#message:EntityIdentifiers
     */
    private static final class EntityIdentifiers {
        @JsonProperty("gateway_ids")
        private GatewayIdentifiers gatewayIds = new GatewayIdentifiers();
    }
    
    /**
     * See https://www.thethingsindustries.com/docs/reference/api/events/#message:GatewayIdentifiers
     */
    @SuppressFBWarnings("URF_UNREAD_FIELD")
    private static final class GatewayIdentifiers {
        @JsonProperty("gateway_id")
        private String gatewayId;
    }
}
