package nl.bertriksikken.ttn.message;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bertriksikken.ttn.message.UplinkMessage.Settings.DataRate;

/**
 * https://www.thethingsindustries.com/docs/reference/api/events/#event:gs.down.send
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class GsDownSendData {

    @JsonProperty("@type")
    String dataType = "";

    @JsonProperty("raw_payload")
    public byte[] rawPayload;

    @JsonProperty("scheduled")
    public Scheduled scheduled = new Scheduled();

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{dataType=%s,rawPayload=%d bytes,scheduled=%s}", dataType, rawPayload.length,
            scheduled);
    }

    public static final class Scheduled {
        @JsonProperty("data_rate")
        public DataRate dataRate = new DataRate();

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
            return String.format(Locale.ROOT,
                "{data_rate=%s,frequency=%d,timestamp=%d,downlink=%s,concentratorTimestamp=%d}", dataRate, frequency,
                timestamp, downlink, concentratorTimestamp);
        }

        public static final class Downlink {
            @JsonProperty("tx_power")
            public double txPower;

            @JsonProperty("invert_polarization")
            public boolean invertPolarization;

            @Override
            public String toString() {
                return String.format(Locale.ROOT, "{tx_power=%.2f,invert_polarization=%s", txPower, invertPolarization);
            }
        }
    }
}
