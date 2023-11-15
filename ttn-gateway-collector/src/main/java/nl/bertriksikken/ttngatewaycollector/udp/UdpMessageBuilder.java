package nl.bertriksikken.ttngatewaycollector.udp;

import java.time.Instant;
import java.util.Locale;

import nl.bertriksikken.ttn.message.GatewayStatus;
import nl.bertriksikken.ttn.message.GatewayStatus.Location;
import nl.bertriksikken.ttn.message.GatewayStatus.Metrics;
import nl.bertriksikken.ttn.message.GsDownSendData;
import nl.bertriksikken.ttn.message.GsDownSendData.Scheduled;
import nl.bertriksikken.ttn.message.UplinkMessage;
import nl.bertriksikken.ttn.message.UplinkMessage.RxMetadata;
import nl.bertriksikken.ttn.message.UplinkMessage.Settings.DataRate;
import nl.bertriksikken.ttngatewaycollector.udp.UdpPullResp.TxPk;
import nl.bertriksikken.ttngatewaycollector.udp.UdpPushData.RxPk;
import nl.bertriksikken.ttngatewaycollector.udp.UdpPushData.Stat;

public final class UdpMessageBuilder {

    public RxPk buildRxPk(UplinkMessage uplink) {
        RxMetadata rxMetadata = uplink.rxMetadata.get(0);
        Instant time = rxMetadata.time;
        long timestamp = rxMetadata.timestamp;
        double frequency = uplink.settings.frequency / 1E6;
        String dataRate = createSFBW(uplink.settings.dataRate);
        String codingRate = uplink.settings.dataRate.lora.codingRate;
        int rssi = rxMetadata.rssi;
        double snr = rxMetadata.snr;
        byte[] data = uplink.rawPayload;
        return new RxPk(time, timestamp, frequency, dataRate, codingRate, rssi, snr, data);
    }

    public TxPk buildTxPk(Instant time, GsDownSendData downlink) {
        Scheduled scheduled = downlink.scheduled;
        long timestamp = scheduled.timestamp;
        double frequency = scheduled.frequency / 1E6;
        String dataRate = createSFBW(scheduled.dataRate);
        String codingRate = scheduled.dataRate.lora.codingRate;
        double power = scheduled.downlink.txPower;
        boolean invert = scheduled.downlink.invertPolarization;
        byte[] data = downlink.rawPayload;
        return new TxPk(time, timestamp, frequency, dataRate, codingRate, power, invert, data);
    }

    public Stat buildStat(Instant time, GatewayStatus status) {
        Double latitude = null;
        Double longitude = null;
        Integer altitude = null;
        if (!status.antennaLocations.isEmpty()) {
            Location location = status.antennaLocations.get(0);
            latitude = location.latitude;
            longitude = location.longitude;
            altitude = location.altitude;
        }
        Metrics metrics = status.metrics;
        return new Stat(time, latitude, longitude, altitude, metrics.rxin, metrics.rxok, metrics.rxfw, metrics.ackr,
            metrics.txin, metrics.txok);
    }

    private String createSFBW(DataRate dataRate) {
        return String.format(Locale.ROOT, "SF%dBW%d", dataRate.lora.spreadingFactor, dataRate.lora.bandWidth / 1000);
    }

}
