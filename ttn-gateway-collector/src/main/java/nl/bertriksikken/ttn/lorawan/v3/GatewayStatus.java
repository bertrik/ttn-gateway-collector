package nl.bertriksikken.ttn.lorawan.v3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * <a href="https://www.thethingsindustries.com/docs/api/reference/grpc/events/#event:gs.status.receive">gs.status.receive</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class GatewayStatus extends AbstractMessage {

    @JsonProperty("@type")
    public static final String TYPE = "type.googleapis.com/ttn.lorawan.v3.GatewayStatus";

    @JsonProperty("time")
    public String time = "";

    @JsonProperty("versions")
    public Map<String, String> versions = new HashMap<>();

    @JsonProperty("antenna_locations")
    public List<Location> antennaLocations = new ArrayList<>();

    @JsonProperty("ip")
    public List<String> ip = new ArrayList<>();

    @JsonProperty("metrics")
    public Metrics metrics = new Metrics();

    public GatewayStatus() {
        super(TYPE);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{%s,time=%s,versions=%s,antennas=%s,ip=%s,metrics=%s}", super.toString(), time, versions, antennaLocations, ip, metrics);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Location(@JsonProperty("latitude") Double latitude,
                           @JsonProperty("longitude") Double longitude,
                           @JsonProperty("altitude") int altitude,
                           @JsonProperty("source") String source) {
        public Location {
            latitude = Objects.requireNonNullElse(latitude, Double.NaN);
            longitude = Objects.requireNonNullElse(longitude, Double.NaN);
            source = Objects.requireNonNullElse(source, "");
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Metrics(@JsonProperty("rxin") int rxin, @JsonProperty("rxok") int rxok,
                          @JsonProperty("rxfw") int rxfw, @JsonProperty("ackr") double ackr,
                          @JsonProperty("txin") int txin, @JsonProperty("txok") int txok) {
        public Metrics() {
            this(0, 0, 0, 0.0, 0, 0);
        }
    }

}
