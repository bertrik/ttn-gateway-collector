package nl.bertriksikken.ttn.eventstream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.bertriksikken.ttn.lorawan.v3.AbstractMessage;
import nl.bertriksikken.ttn.lorawan.v3.EntityIdentifiers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * See
 * https://www.thethingsindustries.com/docs/api/reference/grpc/events/#message:Event
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Event {

    @JsonProperty("name")
    private String name = "";

    @JsonProperty("time")
    private String time = "";

    @JsonProperty("identifiers")
    private List<EntityIdentifiers> identifiers = new ArrayList<>();

    @JsonProperty("data")
    private AbstractMessage data;

    public String getName() {
        return name;
    }

    public Instant getTime() {
        return Instant.parse(time);
    }

    public EntityIdentifiers.GatewayIdentifiers getGatewayIds() {
        if (identifiers.isEmpty()) {
            return null;
        }
        return identifiers.get(0).getGatewayIds();
    }

    public AbstractMessage getData() {
        return data;
    }

    public record Result(@JsonProperty("result") Event event) {
    }
}

