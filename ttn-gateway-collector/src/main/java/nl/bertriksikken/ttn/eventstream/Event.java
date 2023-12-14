package nl.bertriksikken.ttn.eventstream;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

import nl.bertriksikken.ttn.message.EntityIdentifiers;
import nl.bertriksikken.ttn.message.EntityIdentifiers.GatewayIdentifiers;

/**
 * See
 * https://www.thethingsindustries.com/docs/reference/api/events/#message:Event
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
    private JsonNode data = new TextNode("");

    public String getName() {
        return name;
    }

    public Instant getTime() {
        return Instant.parse(time);
    }

    public GatewayIdentifiers getGatewayIds() {
        if (identifiers.isEmpty()) {
            return null;
        }
        return identifiers.get(0).getGatewayIds();
    }

    public JsonNode getData() {
        return data.deepCopy();
    }

}
