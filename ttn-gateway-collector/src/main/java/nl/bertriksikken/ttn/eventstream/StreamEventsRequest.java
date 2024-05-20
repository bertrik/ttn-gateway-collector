package nl.bertriksikken.ttn.eventstream;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.bertriksikken.ttn.lorawan.v3.EntityIdentifiers;

import java.util.ArrayList;
import java.util.List;

/**
 * See https://www.thethingsindustries.com/docs/api/reference/grpc/events/#message:StreamEventsRequest
 */
@JsonAutoDetect(fieldVisibility = Visibility.NONE)
public final class StreamEventsRequest {

    @JsonProperty("identifiers")
    private final List<EntityIdentifiers> identifiers = new ArrayList<>();

    public StreamEventsRequest(String gatewayId) {
        identifiers.add(EntityIdentifiers.createGatewayIds(gatewayId));
    }

}
