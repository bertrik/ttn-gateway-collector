package nl.bertriksikken.ttn.message;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * LoRaWAN uplink message.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class UplinkMessage {

    @JsonProperty("raw_payload")
    public byte[] rawPayload = new byte[0];
    @JsonProperty("payload")
    public Payload payload = new Payload();
    @JsonProperty("settings")
    public Settings settings = new Settings();
    @JsonProperty("rx_metadata")
    public List<RxMetadata> rxMetadata = Arrays.asList(new RxMetadata());

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{raw=<%d bytes>,payload=%s,settings=%s,metadata=%s}", rawPayload.length,
            payload, settings, rxMetadata);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payload {
        @JsonProperty("m_hdr")
        public MHdr mhdr = new MHdr();
        @JsonProperty("mac_payload")
        public MacPayload macPayload = new MacPayload();
        @JsonProperty("join_request_payload")
        public JoinRequestPayload joinRequestPayload; // can be null if absent

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "{mhdr=%s,mac=%s}", mhdr, macPayload);
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class MHdr {
            @JsonProperty("m_type")
            public String mtype = "";

            @Override
            public String toString() {
                return String.format(Locale.ROOT, "{mtype=%s}", mtype);
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class MacPayload {
            @JsonProperty("f_hdr")
            public FHdr fhdr = new FHdr();
            @JsonProperty("f_port")
            public int fport;

            @Override
            public String toString() {
                return String.format(Locale.ROOT, "{fhdr=%s,fport=%d}", fhdr, fport);
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class FHdr {
                @JsonProperty("dev_addr")
                public String devAddr = "";
                @JsonProperty("f_ctrl")
                public FCtrl fctrl = new FCtrl();
                @JsonProperty("f_cnt")
                public int fcnt = 0;

                @Override
                public String toString() {
                    return String.format(Locale.ROOT, "{devAddr=%s,fctrl=%s,fcnt=%d}", devAddr, fctrl, fcnt);
                }

                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class FCtrl {
                    @JsonProperty("adr")
                    public boolean adr = false;

                    @Override
                    public String toString() {
                        return String.format(Locale.ROOT, "{adr=%s}", adr);
                    }
                }
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class JoinRequestPayload {
            @JsonProperty("join_eui")
            public String joinEui = "";
            @JsonProperty("dev_eui")
            public String devEui = "";
            @JsonProperty("dev_nonce")
            public String devNonce = "";
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Settings {
        @JsonProperty("data_rate")
        public DataRate dataRate = new DataRate();
        @JsonProperty("coding_rate")
        public String codingRate;
        @JsonProperty("frequency")
        public int frequency;
        @JsonProperty("time")
        public String time = "";

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "{datarate=%s,frequency=%d,time=%s}", dataRate, frequency, time);
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class DataRate {
            @JsonProperty("lora")
            public Lora lora = new Lora();

            @Override
            public String toString() {
                return String.format(Locale.ROOT, "{lora=%s}", lora);
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Lora {
                @JsonProperty("spreading_factor")
                public int spreadingFactor;
                @JsonProperty("bandwidth")
                public int bandWidth;

                @Override
                public String toString() {
                    return String.format(Locale.ROOT, "{SF=%d,BW=%d}", spreadingFactor, bandWidth);
                }
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RxMetadata {
        @JsonProperty("time")
        public String time;
        
        @JsonProperty("rssi")
        public int rssi;

        @JsonProperty("snr")
        public double snr;

        @JsonProperty("channel_index")
        public int channelIndex;

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "{rssi=%s,snr=%.1f,channel=%s}", rssi, snr, channelIndex);
        }
    }

}
