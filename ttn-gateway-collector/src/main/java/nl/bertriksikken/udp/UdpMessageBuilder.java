package nl.bertriksikken.udp;

import java.time.Instant;
import java.util.Locale;

import nl.bertriksikken.ttn.message.GsDownSendData;
import nl.bertriksikken.ttn.message.GsDownSendData.Scheduled;
import nl.bertriksikken.ttn.message.UplinkMessage;
import nl.bertriksikken.ttn.message.UplinkMessage.RxMetadata;
import nl.bertriksikken.udp.UdpPullRespJson.TxPk;
import nl.bertriksikken.udp.UdpPushDataJson.RxPk;

public final class UdpMessageBuilder {

    public RxPk buildRxPk(UplinkMessage uplink) {
        RxMetadata rxMetadata = uplink.rxMetadata.get(0);
        Instant time = rxMetadata.time;
        long timestamp = rxMetadata.timestamp;
        double frequency = uplink.settings.frequency / 1E6;
        String dataRate = String.format(Locale.ROOT, "SF%dBW%d", uplink.settings.dataRate.lora.spreadingFactor,
            uplink.settings.dataRate.lora.bandWidth / 1000);
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
        String dataRate = String.format(Locale.ROOT, "SF%dBW%d", scheduled.dataRate.lora.spreadingFactor,
            scheduled.dataRate.lora.bandWidth / 1000);
        String codingRate = scheduled.dataRate.lora.codingRate;
        double power = scheduled.downlink.txPower;
        boolean invert = scheduled.downlink.invertPolarization;
        byte[] data = downlink.rawPayload;
        return new TxPk(time, timestamp, frequency, dataRate, codingRate, power, invert, data);
    }

}
