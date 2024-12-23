package nl.bertriksikken.ttngatewaycollector.udp;

import nl.bertriksikken.ttn.lorawan.v3.DownlinkMessage;
import nl.bertriksikken.ttn.lorawan.v3.GatewayStatus;
import nl.bertriksikken.ttn.lorawan.v3.Settings;
import nl.bertriksikken.ttn.lorawan.v3.UplinkMessage;
import nl.bertriksikken.ttngatewaycollector.udp.UdpPullResp.TxPk;
import nl.bertriksikken.ttngatewaycollector.udp.UdpPushData.RxPk;
import nl.bertriksikken.ttngatewaycollector.udp.UdpPushData.Stat;

import java.time.Instant;
import java.util.Locale;

public final class UdpMessageBuilder {

    public RxPk buildRxPk(UplinkMessage uplink) {
        UplinkMessage.RxMetadata rxMetadata = uplink.rxMetadata.get(0);
        Instant time = rxMetadata.time();
        long timestamp = rxMetadata.timestamp();
        double frequency = uplink.settings.frequency() / 1E6;
        String modulation = createModulation(uplink.settings.dataRate());
        String dataRate = createSFBW(uplink.settings.dataRate());
        Settings.DataRate.Lora lora = uplink.settings.dataRate().lora();
        String codingRate = (lora != null) ? lora.codingRate() : "";
        int rssi = rxMetadata.rssi();
        double snr = rxMetadata.snr();
        byte[] data = uplink.rawPayload;
        return new RxPk(time, timestamp, frequency, modulation, dataRate, codingRate, rssi, snr, data);
    }

    public TxPk buildTxPk(Instant time, DownlinkMessage downlink) {
        DownlinkMessage.Scheduled scheduled = downlink.scheduled;
        long timestamp = scheduled.timestamp;
        double frequency = scheduled.frequency / 1E6;
        String modulation = createModulation(scheduled.dataRate);
        String dataRate = createSFBW(scheduled.dataRate);
        String codingRate = scheduled.dataRate.lora().codingRate();
        double power = scheduled.downlink.txPower();
        boolean invert = scheduled.downlink.invertPolarization();
        byte[] data = downlink.rawPayload;
        return new TxPk(time, timestamp, frequency, modulation, dataRate, codingRate, power, invert, data);
    }

    public Stat buildStat(Instant time, GatewayStatus status) {
        Double latitude = null;
        Double longitude = null;
        Integer altitude = null;
        if (!status.antennaLocations.isEmpty()) {
            GatewayStatus.Location location = status.antennaLocations.get(0);
            latitude = location.latitude();
            longitude = location.longitude();
            altitude = location.altitude();
        }
        GatewayStatus.Metrics metrics = status.metrics;
        return new Stat(time, latitude, longitude, altitude, metrics.rxin(), metrics.rxok(), metrics.rxfw(), metrics.ackr(),
                metrics.txin(), metrics.txok());
    }

    private String createSFBW(Settings.DataRate dataRate) {
        Settings.DataRate.Fsk fsk = dataRate.fsk();
        if ((fsk != null) && fsk.bitRate() > 0) {
            return String.format(Locale.ROOT, "%d", dataRate.fsk().bitRate());
        }
        return String.format(Locale.ROOT, "SF%dBW%d", dataRate.lora().spreadingFactor(), dataRate.lora().bandWidth() / 1000);
    }

    private String createModulation(Settings.DataRate dataRate) {
        Settings.DataRate.Fsk fsk = dataRate.fsk();
        return (fsk != null) && (fsk.bitRate() > 0) ? "FSK" : "LORA";
    }

}
