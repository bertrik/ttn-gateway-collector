package nl.bertriksikken.ttn.lorawan.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Locale;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "@type", defaultImpl = Void.class)
@JsonSubTypes({@JsonSubTypes.Type(name = DownlinkMessage.TYPE, value = DownlinkMessage.class),
        @JsonSubTypes.Type(name = GatewayUplinkMessage.TYPE, value = GatewayUplinkMessage.class),
        @JsonSubTypes.Type(name = GatewayStatus.TYPE, value = GatewayStatus.class)})
public abstract class AbstractMessage {
    @JsonProperty("@type")
    private final String type;

    protected AbstractMessage(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "type=%s", type);
    }
}

