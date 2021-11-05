package nl.bertriksikken.ttngatewaycollector.export;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.bertriksikken.ttn.message.UplinkMessage;
import nl.bertriksikken.ttn.message.UplinkMessage.Payload.JoinRequestPayload;
import nl.bertriksikken.ttn.message.UplinkMessage.RxMetadata;

/**
 * Represents one line in the export.
 */
@JsonPropertyOrder({ "time", "gateway", "frequency", "sf", "snr", "rssi", "raw_payload", "type", "dev_addr", "port",
    "fcnt", "adr", "join_eui", "dev_eui", "dev_nonce" })
public final class ExportEvent {

    @JsonProperty("time")
    final String time;
    @JsonProperty("gateway")
    final String gateway;

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

    private ExportEvent(String time, String gateway, byte[] rawPayload, int spreadingFactor, int frequency, double snr,
        int rssi) {
        this.time = time;
        this.gateway = gateway;
        this.rawPayload = rawPayload;
        this.sf = spreadingFactor;
        this.frequency = frequency;
        this.snr = snr;
        this.rssi = rssi;
    }

    public static ExportEvent fromUplinkMessage(String gateway, UplinkMessage message) {
        byte[] rawPayload = message.rawPayload;
        int spreadingFactor = message.settings.dataRate.lora.spreadingFactor;
        int frequency = message.settings.frequency;
        
        RxMetadata rxMetadata = message.rxMetadata.get(0);
        String time = rxMetadata.time;
        double snr = rxMetadata.snr;
        int rssi = rxMetadata.rssi;
        ExportEvent event = new ExportEvent(time, gateway, rawPayload, spreadingFactor, frequency, snr, rssi);

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

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{%s,%s,%d,%d,%f,%d,%d,%s,%s,%d,%d,%s,%s,%s,%s}", gateway, time, frequency,
            sf, snr, rssi, rawPayload.length, packetType, devAddr, fport, fcnt, adr, joinEui, devEui, devNonce);
    }

}
