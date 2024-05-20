package nl.bertriksikken.ttn.lorawan.v3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

/**
 * https://www.thethingsindustries.com/docs/api/reference/grpc/events/#event:gs.status.receive
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
    public static final class Location {
        @JsonProperty("latitude")
        public double latitude = Double.NaN;
        @JsonProperty("longitude")
        public double longitude = Double.NaN;
        @JsonProperty("altitude")
        public int altitude = 0;
        @JsonProperty("source")
        public String source = "";

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "{lat=%.6f,lon=%.6f,alt=%d,source=%s}", latitude, longitude, altitude, source);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Metrics {
        @JsonProperty("rxin")
        public int rxin;
        @JsonProperty("rxok")
        public int rxok;
        @JsonProperty("rxfw")
        public int rxfw;
        @JsonProperty("ackr")
        public double ackr = Double.NaN;
        @JsonProperty("txin")
        public int txin;
        @JsonProperty("txok")
        public int txok;
    }

}
