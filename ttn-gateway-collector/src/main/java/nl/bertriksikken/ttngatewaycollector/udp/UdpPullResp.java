package nl.bertriksikken.ttngatewaycollector.udp;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON structure according to
 * https://github.com/Lora-net/packet_forwarder/blob/master/PROTOCOL.TXT
 */
public final class UdpPullResp {

    public static final byte IDENTIFIER = 3;
    
    @JsonProperty("txpk")
    @SuppressWarnings("unused")
    private final TxPk txPk;

    UdpPullResp(TxPk txPk) {
        this.txPk = txPk;
    }

    public static final class TxPk {
        @JsonProperty("time")
        final String time;
        @JsonProperty("imme")
        final boolean imme;
        @JsonProperty("tmst")
        final long tmst;
        @JsonProperty("freq")
        final double freq;
        @JsonProperty("rfch")
        final int chain;
        @JsonProperty("powe")
        final double powe;
        @JsonProperty("modu")
        final String modu;
        @JsonProperty("datr")
        final String datr;
        @JsonProperty("codr")
        final String codr;
        @JsonProperty("ipol")
        final boolean ipol;
        @JsonProperty("size")
        final int size;
        @JsonProperty("data")
        final byte[] data;

        public TxPk(Instant time, long timestamp, double frequency, String modulation, String dataRate,
            String codingRate, double power, boolean invertPol, byte[] data) {
            this.time = time.truncatedTo(ChronoUnit.MICROS).toString();
            this.imme = false;
            this.tmst = timestamp;
            this.chain = 0;
            this.freq = frequency;
            this.modu = modulation;
            this.datr = dataRate;
            this.codr = codingRate;
            this.ipol = invertPol;
            this.powe = power;
            this.size = data.length;
            this.data = data.clone();
        }
    }

}
