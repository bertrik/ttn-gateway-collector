package nl.bertriksikken.ttngatewaycollector.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import nl.bertriksikken.lorawan.AirTimeCalculator;
import nl.bertriksikken.lorawan.LorawanPacket;
import nl.bertriksikken.lorawan.MType;
import nl.bertriksikken.ttn.lorawan.v3.DownlinkMessage;
import nl.bertriksikken.ttn.lorawan.v3.Settings;
import nl.bertriksikken.ttn.lorawan.v3.UplinkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Represents one line in the export.
 */
@JsonPropertyOrder({"time", "gateway", "frequency", "modulation", "snr", "rssi", "airtime", "raw_payload", "type", "dev_addr", "port", "fcnt", "adr", "join_eui", "dev_eui", "dev_nonce"})
public final class ExportEvent {

    private static final Logger LOG = LoggerFactory.getLogger(ExportEvent.class);

    private static final AirTimeCalculator airTimeCalculator = AirTimeCalculator.LORAWAN;

    @JsonProperty("time")
    final String time;
    @JsonProperty("gateway")
    final String gateway;

    // radio settings
    @JsonProperty("frequency")
    final int frequency;
    @JsonProperty("modulation")
    final String modulation;
    @JsonProperty("snr")
    final double snr;
    @JsonProperty("rssi")
    final double rssi;
    @JsonProperty("airtime")
    final BigDecimal airtime;

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

    private ExportEvent(Instant time, String gateway, byte[] rawPayload, String modulation, int frequency, double snr, double rssi, double airtime) {
        this.time = time.truncatedTo(ChronoUnit.MILLIS).toString();
        this.gateway = gateway;
        this.rawPayload = rawPayload;
        this.modulation = modulation;
        this.frequency = frequency;
        this.snr = snr;
        this.rssi = rssi;
        this.airtime = BigDecimal.valueOf(airtime).setScale(6, RoundingMode.HALF_UP);
    }

    public static ExportEvent fromUplinkMessage(UplinkMessage message) {
        UplinkMessage.RxMetadata rxMetadata = message.rxMetadata().get(0);
        Instant time = rxMetadata.time();
        String gatewayId = rxMetadata.gatewayIds().gatewayId();
        byte[] rawPayload = message.rawPayload();
        String modulation = getModulation(message.settings().dataRate());
        int frequency = message.settings().frequency();
        double snr = rxMetadata.snr();
        int rssi = rxMetadata.rssi();
        double airtime = airTimeCalculator.calculate(message.settings().dataRate(), rawPayload.length);
        ExportEvent event = new ExportEvent(time, gatewayId, rawPayload, modulation, frequency, snr, rssi, airtime);

        UplinkMessage.Payload.JoinRequestPayload joinRequestPayload = message.payload().joinRequestPayload();
        if (joinRequestPayload != null) {
            event.packetType = MType.JOIN_REQUEST.toString();
            event.joinEui = joinRequestPayload.joinEui();
            event.devEui = joinRequestPayload.devEui();
            event.devNonce = joinRequestPayload.devNonce();
        } else {
            event.packetType = message.payload().mhdr().mtype();
        }

        event.devAddr = message.payload().macPayload().fhdr().devAddr();
        event.fport = message.payload().macPayload().fport();
        event.fcnt = message.payload().macPayload().fhdr().fcnt();
        event.adr = message.payload().macPayload().fhdr().fctrl().adr();
        return event;
    }

    private static String getModulation(Settings.DataRate dataRate) {
        var lora = dataRate.lora();
        if (lora != null) {
            return String.format(Locale.ROOT, "SF%dBW%d", lora.spreadingFactor(), lora.bandWidth() / 1000);
        }
        var fsk = dataRate.fsk();
        if (fsk != null) {
            return String.format(Locale.ROOT, "FSK%d", fsk.bitRate() / 1000);
        }
        return "?";
    }

    public static ExportEvent fromDownlinkData(Instant time, String gateway, DownlinkMessage data) {
        double airtime = airTimeCalculator.calculate(data.scheduled.dataRate, data.rawPayload.length);
        String modulation = getModulation(data.scheduled.dataRate);
        ExportEvent event = new ExportEvent(time, gateway, data.rawPayload, modulation, data.scheduled.frequency, 0.0,
                data.scheduled.downlink.txPower(), airtime);
        try {
            LorawanPacket packet = LorawanPacket.decode(data.rawPayload);
            event.packetType = MType.fromMhdr(packet.mhdr).getDescription();
            event.devAddr = packet.macPayload.devAddr;
            event.fport = packet.macPayload.fport;
            event.fcnt = packet.macPayload.fcnt;
            event.adr = packet.macPayload.adr;
        } catch (IOException e) {
            LOG.warn("Could not decode payload: {}", e.getMessage());
        }
        return event;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{%s,%s,%d,%s,%f,%f,%d,%s,%s,%d,%d,%s,%s,%s,%s}",
                gateway, time, frequency, modulation, snr, rssi, rawPayload.length, packetType, devAddr, fport, fcnt, adr, joinEui, devEui, devNonce);
    }

}
