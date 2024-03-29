package nl.bertriksikken.ttn.eventstream;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bertriksikken.ttn.message.EntityIdentifiers;

/**
 * See https://www.thethingsindustries.com/docs/reference/api/events/#message:StreamEventsRequest
 */
@JsonAutoDetect(fieldVisibility = Visibility.NONE)
public final class StreamEventsRequest {
    
    @JsonProperty("identifiers")
    private final List<EntityIdentifiers> identifiers = new ArrayList<>();
    
    public StreamEventsRequest(String gatewayId) {
        identifiers.add(EntityIdentifiers.createGatewayIds(gatewayId));
    }
    
}
