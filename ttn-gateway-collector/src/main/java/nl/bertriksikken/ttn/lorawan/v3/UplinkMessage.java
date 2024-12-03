package nl.bertriksikken.ttn.lorawan.v3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class UplinkMessage {

    @JsonProperty("raw_payload")
    public byte[] rawPayload = new byte[0];
    @JsonProperty("payload")
    public Payload payload = new Payload();
    @JsonProperty("settings")
    public Settings settings = new Settings();
    @JsonProperty("rx_metadata")
    public List<RxMetadata> rxMetadata = List.of(new RxMetadata());

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{raw=<%d bytes>,payload=%s,settings=%s,metadata=%s}", rawPayload.length, payload, settings, rxMetadata);
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
        public record MHdr(@JsonProperty("m_type") String mtype) {
            public MHdr {
                mtype = Objects.requireNonNullElse(mtype, "");
            }

            public MHdr() {
                this(null);
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record MacPayload(@JsonProperty("f_hdr") FHdr fhdr,
                                 @JsonProperty("f_port") int fport) {
            public MacPayload {
                fhdr = Objects.requireNonNullElse(fhdr, new FHdr());
            }

            public MacPayload() {
                this(null, 0);
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            public record FHdr(@JsonProperty("dev_addr") String devAddr,
                               @JsonProperty("f_ctrl") FCtrl fctrl,
                               @JsonProperty("f_cnt") int fcnt) {
                public FHdr {
                    devAddr = Objects.requireNonNullElse(devAddr, "");
                    fctrl = Objects.requireNonNullElse(fctrl, new FCtrl(false));
                }

                public FHdr() {
                    this(null, null, 0);
                }

                @JsonIgnoreProperties(ignoreUnknown = true)
                public record FCtrl(@JsonProperty("adr") boolean adr) {
                }
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record JoinRequestPayload(@JsonProperty("join_eui") String joinEui,
                                         @JsonProperty("dev_eui") String devEui,
                                         @JsonProperty("dev_nonce") String devNonce) {
            public JoinRequestPayload {
                joinEui = Objects.requireNonNullElse(joinEui, "");
                devEui = Objects.requireNonNullElse(devEui, "");
                devNonce = Objects.requireNonNullElse(devNonce, "");
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RxMetadata {
        @JsonProperty("gateway_ids")
        public EntityIdentifiers.GatewayIdentifiers gatewayIds = EntityIdentifiers.GatewayIdentifiers.create("", "");

        @JsonProperty("time")
        public Instant time = Instant.now();

        @JsonProperty("timestamp")
        public long timestamp;

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
