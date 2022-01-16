package nl.bertriksikken.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.bertriksikken.ttn.message.UplinkMessage;
import nl.bertriksikken.ttn.message.UplinkMessage.RxMetadata;

public final class UdpProtocolSender {

    private static final Logger LOG = LoggerFactory.getLogger(UdpProtocolSender.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final UdpProtocolConfig config;

    private InetAddress udpAddress;
    private DatagramSocket udpSocket;
    
    private AtomicInteger token = new AtomicInteger(0);

    public UdpProtocolSender(UdpProtocolConfig config) {
        this.config = config;
    }

    public void start() throws IOException {
        LOG.info("Starting");
        if (!config.host.isEmpty()) {
            udpAddress = InetAddress.getByName(config.host);
        }
        udpSocket = new DatagramSocket();
    }

    public void stop() {
        LOG.info("Stopping");
        udpSocket.close();
        executor.shutdownNow();
    }

    public void send(String euiString, UplinkMessage uplink) {
        // decode EUI
        byte[] eui = parseHex(euiString);

        // build packet
        UdpPushData pushData = new UdpPushData(eui, token.incrementAndGet());
        RxMetadata rxMetadata = uplink.rxMetadata.get(0);
        Instant time = rxMetadata.time;
        double frequency = uplink.settings.frequency / 1E6;
        String dataRate = String.format(Locale.ROOT, "SF%dBW%d", uplink.settings.dataRate.lora.spreadingFactor,
            uplink.settings.dataRate.lora.bandWidth / 1000);
        String codingRate = uplink.settings.codingRate;
        int rssi = rxMetadata.rssi;
        double snr = rxMetadata.snr;
        byte[] data = uplink.rawPayload;
        pushData.addPacket(time, frequency, dataRate, codingRate, rssi, snr, data);

        // schedule for transmission
        if (udpAddress != null) {
            executor.execute(() -> sendUdp(pushData, udpAddress, config.port, udpSocket));
        }
    }

    private void sendUdp(UdpPushData pushData, InetAddress address, int port, DatagramSocket socket) {
        try {
            // send it
            byte[] udpData = pushData.encode();
            DatagramPacket dgram = new DatagramPacket(udpData, udpData.length, address, port);
            socket.send(dgram);
        } catch (IOException e) {
            LOG.warn("Caught IOException", e);
        }
    }

    private byte[] parseHex(String hexString) {
        byte[] data = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            data[i / 2] = (byte) Integer.parseInt(hexString.substring(i, i + 2), 16);
        }
        return data;
    }

}
