package nl.bertriksikken.ttn.eventstream;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * See https://www.thethingsindustries.com/docs/reference/api/events/#message:Event
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Event {

    @JsonProperty("name")
    String name = "";
    
    @JsonProperty("time")
    String time = "";
    
    @JsonProperty("identifiers")
    JsonNode identifiers;
    
    @JsonProperty("data")
    JsonNode data = new TextNode("");

    public String getName() {
        return name;
    }

    public Instant getTime() {
        return Instant.parse(time);
    }

//    public List<JsonNode> getIdentifiers() {
    public JsonNode getIdentifiers() {
        return identifiers;
    }

    public JsonNode getData() {
        return data.deepCopy();
    }
    
}
