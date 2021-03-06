package nl.bertriksikken.udp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON structure according to
 * https://github.com/Lora-net/packet_forwarder/blob/master/PROTOCOL.TXT
 */
public final class UdpPushDataJson {

    @JsonProperty("rxpk")
    final List<RxPk> packets = new ArrayList<>();

    void addReceiveData(Instant time, double frequency, String dataRate, String codingRate, int rssi, double snr,
        byte[] data) {
        packets.add(new RxPk(time, frequency, dataRate, codingRate, rssi, snr, data));
    }

    private static final class RxPk {
        @JsonProperty("time")
        final String time;
        @JsonProperty("tmms")
        final long tmms;
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
        String codr;
        @JsonProperty("rssi")
        int rssi;
        @JsonProperty("lsnr")
        double lsnr;
        @JsonProperty("size")
        int size;
        @JsonProperty("data")
        byte[] data;

        public RxPk(Instant time, double frequency, String dataRate, String codingRate, int rssi, double snr,
            byte[] data) {
            this.time = time.toString();
            this.tmms = 0;
            this.tmst = 0;
            this.chan = 0;
            this.chain = 0;
            this.freq = frequency;
            this.stat = 1;
            this.modu = "LORA";
            this.datr = dataRate;
            this.codr = codingRate;
            this.rssi = rssi;
            this.lsnr = snr;
            this.size = data.length;
            this.data = data.clone();
        }

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "{%s,%f,%s,%s,%d,%.1f,%d,%s,%d,%s}", time, freq, stat, modu,
                datr, codr, rssi, lsnr, size, data);
        }
    }
}
