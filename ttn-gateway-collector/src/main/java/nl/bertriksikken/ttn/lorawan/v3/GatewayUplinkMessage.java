package nl.bertriksikken.ttn.lorawan.v3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class GatewayUplinkMessage extends AbstractMessage {

    public static final String TYPE = "type.googleapis.com/ttn.lorawan.v3.GatewayUplinkMessage";

    @JsonProperty("message")
    private UplinkMessage message;

    public GatewayUplinkMessage() {
        super(TYPE);
    }

    public UplinkMessage getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{%s,message=%s}", super.toString(), message);
    }

}
