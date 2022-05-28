package nl.bertriksikken.ttn.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class GsUpReceiveData {

    @JsonProperty("@type")
    String dataType = "";

    @JsonProperty("message")
    JsonNode message = new TextNode("");

    public String getDataType() {
        return dataType;
    }

    public JsonNode getMessage() {
        return message;
    }

}
