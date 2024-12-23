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
    public record Payload(@JsonProperty("m_hdr") MHdr mhdr,
                          @JsonProperty("mac_payload") MacPayload macPayload,
                          @JsonProperty("join_request_payload") JoinRequestPayload joinRequestPayload) {
        public Payload {
            mhdr = Objects.requireNonNullElse(mhdr, new MHdr());
            macPayload = Objects.requireNonNullElse(macPayload, new MacPayload());
            // joinRequestPayload can be null if absent
        }

        public Payload() {
            this(null, null, null);
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
    public record RxMetadata(@JsonProperty("gateway_ids") EntityIdentifiers.GatewayIdentifiers gatewayIds,
                             @JsonProperty("time") Instant time,
                             @JsonProperty("timestamp") long timestamp,
                             @JsonProperty("rssi") int rssi,
                             @JsonProperty("snr") double snr,
                             @JsonProperty("channel_index") int channelIndex) {

        public RxMetadata {
            gatewayIds = Objects.requireNonNullElse(gatewayIds, EntityIdentifiers.GatewayIdentifiers.create("", ""));
            time = Objects.requireNonNullElse(time, Instant.now());
        }
        public RxMetadata() {
            this(null, null, 0, 0, 0.0, 0);
        }
    }

}
