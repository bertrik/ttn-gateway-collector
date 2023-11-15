package nl.bertriksikken.ttngatewaycollector.udp;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON structure according to
 * https://github.com/Lora-net/packet_forwarder/blob/master/PROTOCOL.TXT
 */
@JsonInclude(Include.NON_NULL)
public final class UdpPushData {

    @JsonProperty("rxpk")
    private final List<RxPk> packets;

    @JsonProperty("stat")
    private final Stat stat;

    UdpPushData(RxPk packet) {
        this.packets = List.of(packet);
        this.stat = null;
    }

    UdpPushData(Stat stat) {
        this.packets = null;
        this.stat = stat;
    }

    @JsonInclude(Include.NON_NULL)
    public static final class RxPk {
        @JsonProperty("time")
        final String time;
        @JsonProperty("tmst")
        final long tmst;
        @JsonProperty("chan")
        final int chan;
        @JsonProperty("rfch")
        final int chain;
        @JsonProperty("freq")
        double freq;
        @JsonProperty("stat")
        int stat;
        @JsonProperty("modu")
        String modu;
        @JsonProperty("datr")
        String datr;
        @JsonProperty("codr")
        String codr; // only for LORA
        @JsonProperty("rssi")
        int rssi;
        @JsonProperty("lsnr")
        double lsnr;
        @JsonProperty("size")
        int size;
        @JsonProperty("data")
        byte[] data;

        public RxPk(Instant time, long timestamp, double frequency, String modulation, String dataRate,
            String codingRate, int rssi, double snr, byte[] data) {
            this.time = time.truncatedTo(ChronoUnit.MICROS).toString();
            this.tmst = timestamp;
            this.chan = 0;
            this.chain = 0;
            this.freq = frequency;
            this.stat = 1;
            this.modu = modulation;
            this.datr = dataRate;
            this.codr = codingRate;
            this.rssi = rssi;
            this.lsnr = snr;
            this.size = data.length;
            this.data = data.clone();
        }

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "{%s,%f,%s,%s,%d,%.1f,%d,%s,%d,%s}", time, freq, stat, modu, datr, codr,
                rssi, lsnr, size, data);
        }
    }

    @JsonInclude(Include.NON_NULL)
    public static final class Stat {
        @JsonProperty("time")
        final String time;
        @JsonProperty("lati")
        final Double latitude;
        @JsonProperty("long")
        final Double longitude;
        @JsonProperty("alti")
        final Integer altitude;
        @JsonProperty("rxnb")
        final int rxnb;
        @JsonProperty("rxok")
        final int rxok;
        @JsonProperty("rxfw")
        final int rxfw;
        @JsonProperty("ackr")
        final double ackr;
        @JsonProperty("dwnb")
        final int dwnb;
        @JsonProperty("txnb")
        final int txnb;

        public Stat(Instant time, Double latitude, Double longitude, Integer altitude, int rxnb, int rxok, int rxfw,
            double ackr, int dwnb, int txnb) {
            this.time = time.truncatedTo(ChronoUnit.MICROS).toString();
            this.latitude = latitude;
            this.longitude = longitude;
            this.altitude = altitude;
            this.rxnb = rxnb;
            this.rxok = rxok;
            this.rxfw = rxfw;
            this.ackr = ackr;
            this.dwnb = dwnb;
            this.txnb = txnb;
        }
    }

}
