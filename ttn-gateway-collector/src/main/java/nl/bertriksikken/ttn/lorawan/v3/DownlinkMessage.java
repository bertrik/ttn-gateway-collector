package nl.bertriksikken.ttn.lorawan.v3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Locale;

/**
 * <a href="https://www.thethingsindustries.com/docs/reference/api/events/#event:gs.down.send">gs.down.send</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DownlinkMessage extends AbstractMessage {

    public static final String TYPE = "type.googleapis.com/ttn.lorawan.v3.DownlinkMessage";
    @JsonProperty("raw_payload")
    public byte[] rawPayload;
    @JsonProperty("scheduled")
    public Scheduled scheduled = new Scheduled();

    public DownlinkMessage() {
        super(TYPE);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{%s,raw=<%d bytes>,scheduled=%s}", super.toString(), rawPayload.length, scheduled);
    }

    public static final class Scheduled {
        @JsonProperty("data_rate")
        public Settings.DataRate dataRate = new Settings.DataRate(new Settings.DataRate.Lora());

        @JsonProperty("frequency")
        public int frequency;

        @JsonProperty("timestamp")
        public long timestamp;

        @JsonProperty("downlink")
        public Downlink downlink = new Downlink();

        @JsonProperty("concentrator_timestamp")
        public long concentratorTimestamp;

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "{data_rate=%s,frequency=%d,timestamp=%d,downlink=%s,concentratorTimestamp=%d}", dataRate, frequency, timestamp, downlink, concentratorTimestamp);
        }

        public record Downlink(@JsonProperty("tx_power") double txPower,
                               @JsonProperty("invert_polarization") boolean invertPolarization) {
            public Downlink() {
                this(Double.NaN, false);
            }
        }
    }
}
