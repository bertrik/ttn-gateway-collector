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
        final long gpsTime;
        @JsonProperty("chan")
        final int channel;
        @JsonProperty("rfch")
        final int chain;
        @JsonProperty("freq")
        double frequency;
        @JsonProperty("stat")
        int crc;
        @JsonProperty("modu")
        String modulation;
        @JsonProperty("datr")
        String dataRate;
        @JsonProperty("codr")
        String codingRate;
        @JsonProperty("rssi")
        int rssi;
        @JsonProperty("lsnr")
        double snr;
        @JsonProperty("size")
        int size;
        @JsonProperty("data")
        byte[] data;

        public RxPk(Instant time, double frequency, String dataRate, String codingRate, int rssi, double snr,
            byte[] data) {
            this.time = time.toString();
            this.tmms = 0;
            this.gpsTime = 0;
            this.channel = 0;
            this.chain = 0;
            this.frequency = frequency;
            this.crc = 1;
            this.modulation = "LORA";
            this.dataRate = dataRate;
            this.codingRate = codingRate;
            this.rssi = rssi;
            this.snr = snr;
            this.size = data.length;
            this.data = data.clone();
        }

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "{%s,%f,%s,%s,%d,%.1f,%d,%s,%d,%s}", time, frequency, crc, modulation,
                dataRate, codingRate, rssi, snr, size, data);
        }
    }
}
