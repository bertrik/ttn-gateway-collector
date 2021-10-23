package nl.bertriksikken.ttngatewaycollector.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.bertriksikken.ttn.message.UplinkMessage;
import nl.bertriksikken.ttn.message.UplinkMessage.Payload.JoinRequestPayload;

/**
 * Represents one line in the export.
 */
@JsonPropertyOrder({ "gateway", "time", "frequency", "sf", "snr", "rssi", "raw_payload", "type", "dev_addr", "port",
    "fcnt", "adr", "join_eui", "dev_eui", "dev_nonce"})
public final class ExportEvent {

    @JsonProperty("gateway")
    final String gateway;
    @JsonProperty("time")
    final String time;

    // radio settings
    @JsonProperty("frequency")
    final int frequency;
    @JsonProperty("sf")
    final int sf;
    @JsonProperty("snr")
    final double snr;
    @JsonProperty("rssi")
    final int rssi;

    @JsonProperty("raw_payload")
    final byte[] rawPayload;

    @JsonProperty("type")
    String packetType;
    @JsonProperty("dev_addr")
    String devAddr;
    @JsonProperty("port")
    int fport;
    @JsonProperty("fcnt")
    int fcnt;
    @JsonProperty("adr")
    boolean adr;

    // OTAA join specific
    @JsonProperty("join_eui")
    String joinEui = "";
    @JsonProperty("dev_eui")
    String devEui = "";
    @JsonProperty("dev_nonce")
    String devNonce = "";

    enum EPacketType {
        JOIN_REQUEST, UNCONFIRMED_UPLINK, CONFIRMED_UPLINK
    }

    private ExportEvent(String gateway, String time, byte[] rawPayload, int spreadingFactor, int frequency, double snr,
        int rssi) {
        this.gateway = gateway;
        this.time = time;
        this.rawPayload = rawPayload;
        this.sf = spreadingFactor;
        this.frequency = frequency;
        this.snr = snr;
        this.rssi = rssi;
    }

    public static ExportEvent fromUplinkMessage(String gateway, UplinkMessage message) {
        String time = message.settings.time;
        byte[] rawPayload = message.rawPayload;
        int spreadingFactor = message.settings.dataRate.lora.spreadingFactor;
        int frequency = message.settings.frequency;
        double snr = message.rxMetadata.get(0).snr;
        int rssi = message.rxMetadata.get(0).rssi;
        ExportEvent event = new ExportEvent(gateway, time, rawPayload, spreadingFactor, frequency, snr, rssi);

        JoinRequestPayload joinRequestPayload = message.payload.joinRequestPayload;
        if (joinRequestPayload != null) {
            event.packetType = EPacketType.JOIN_REQUEST.toString();
            event.joinEui = joinRequestPayload.joinEui;
            event.devEui = joinRequestPayload.devEui;
            event.devNonce = joinRequestPayload.devNonce;
        } else {
            event.packetType = message.payload.mhdr.mtype;
        }

        event.devAddr = message.payload.macPayload.fhdr.devAddr;
        event.fport = message.payload.macPayload.fport;
        event.fcnt = message.payload.macPayload.fhdr.fcnt;
        event.adr = message.payload.macPayload.fhdr.fctrl.adr;
        return event;
    }

}
