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
    byte[] rawPayload = new byte[0];
    @JsonProperty("payload")
    Payload payload = new Payload();
    @JsonProperty("settings")
    Settings settings = new Settings();
    @JsonProperty("rx_metadata")
    List<RxMetadata> rxMetadata = Arrays.asList(new RxMetadata());

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{raw=<%d bytes>,payload=%s,settings=%s,metadata=%s}", rawPayload.length,
            payload, settings, rxMetadata);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Payload {
        @JsonProperty("m_hdr")
        MHdr mhdr = new MHdr();
        @JsonProperty("mac_payload")
        MacPayload macPayload = new MacPayload();

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "{mhdr=%s,mac=%s}", mhdr, macPayload);
        }

        static class MHdr {
            @JsonProperty("m_type")
            EMType mtype = EMType.UNDETERMINED;

            @Override
            public String toString() {
                return String.format(Locale.ROOT, "{mtype=%s}", mtype);
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class MacPayload {
            @JsonProperty("f_hdr")
            FHdr fhdr = new FHdr();
            @JsonProperty("f_port")
            int fport;

            @Override
            public String toString() {
                return String.format(Locale.ROOT, "{fhdr=%s,fport=%d}", fhdr, fport);
            }

            static class FHdr {
                @JsonProperty("dev_addr")
                String devAddr = "";
                @JsonProperty("f_ctrl")
                FCtrl fctrl = new FCtrl();
                @JsonProperty("f_cnt")
                int fcnt = 0;

                @Override
                public String toString() {
                    return String.format(Locale.ROOT, "{devAddr=%s,fctrl=%s,fcnt=%d}", devAddr, fctrl, fcnt);
                }

                static class FCtrl {
                    @JsonProperty("adr")
                    boolean adr = false;

                    @Override
                    public String toString() {
                        return String.format(Locale.ROOT, "{adr=%s}", adr);
                    }
                }
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Settings {
        @JsonProperty("data_rate")
        DataRate dataRate = new DataRate();
        @JsonProperty("frequency")
        int frequency;
        @JsonProperty("timestamp")
        long timeStamp;
        @JsonProperty("time")
        String time = "";

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "{datarate=%s,frequency=%d,timestamp=%d,time=%s}", dataRate, frequency,
                timeStamp, time);
        }

        static class DataRate {
            @JsonProperty("lora")
            Lora lora;

            @Override
            public String toString() {
                return String.format(Locale.ROOT, "{lora=%s}", lora);
            }

            static class Lora {
                @JsonProperty("bandwidth")
                int bandWidth;
                @JsonProperty("spreading_factor")
                int spreadingFactor;

                @Override
                public String toString() {
                    return String.format(Locale.ROOT, "{bandWidth=%s,spreadingFactor=%d}", bandWidth, spreadingFactor);
                }
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class RxMetadata {
        @JsonProperty("rssi")
        int rssi;

        @JsonProperty("snr")
        double snr;

        @JsonProperty("channel_index")
        int channel;

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "{rssi=%s,snr=%.1f,channel=%s}", rssi, snr, channel);
        }
    }

    enum EMType {
        UNDETERMINED, UNCONFIRMED_UP, CONFIRMED_UP
    }

}
