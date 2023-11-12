package nl.bertriksikken.ttn.eventstream;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

import nl.bertriksikken.ttn.message.GatewayIdentifier;
import nl.bertriksikken.ttn.message.GatewayIdentifier.GatewayIds;

/**
 * See
 * https://www.thethingsindustries.com/docs/reference/api/events/#message:Event
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Event {

    @JsonProperty("name")
    String name = "";

    @JsonProperty("time")
    String time = "";

    @JsonProperty("identifiers")
    List<GatewayIdentifier> identifiers = new ArrayList<>();

    @JsonProperty("data")
    JsonNode data = new TextNode("");

    public String getName() {
        return name;
    }

    public Instant getTime() {
        return Instant.parse(time);
    }

    public GatewayIds getGatewayIds() {
        if (identifiers.isEmpty()) {
            return null;
        }
        return identifiers.get(0).gatewayId;
    }

    public JsonNode getData() {
        return data.deepCopy();
    }

}
