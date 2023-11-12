package nl.bertriksikken.ttngatewaycollector.udp;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.ttn.message.GatewayIdentifier.GatewayIds;
import nl.bertriksikken.ttn.message.GatewayStatus;
import nl.bertriksikken.ttn.message.GsDownSendData;
import nl.bertriksikken.ttn.message.UplinkMessage;
import nl.bertriksikken.ttn.message.UplinkMessage.RxMetadata;
import nl.bertriksikken.ttngatewaycollector.IEventProcessor;
import nl.bertriksikken.ttngatewaycollector.udp.UdpPullRespJson.TxPk;
import nl.bertriksikken.ttngatewaycollector.udp.UdpPushDataJson.RxPk;
import nl.bertriksikken.ttngatewaycollector.udp.UdpPushDataJson.Stat;

public final class UdpSender implements IEventProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(UdpSender.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final byte PROTOCOL_VERSION = 2;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicInteger token = new AtomicInteger(0);
    private final UdpMessageBuilder udpMessageBuilder = new UdpMessageBuilder();
    private final UdpSenderConfig config;

    private InetAddress inetAddress;
    private DatagramSocket udpSocket;

    public UdpSender(UdpSenderConfig config) {
        this.config = config;
    }

    public void start() throws IOException {
        if (!config.host.isEmpty()) {
            LOG.info("Starting UDP sender for {}:{}", config.host, config.port);
            inetAddress = InetAddress.getByName(config.host);
            udpSocket = new DatagramSocket();
        } else {
            udpSocket = null;
        }
    }

    public void stop() {
        LOG.info("Stopping");
        udpSocket.close();
        executor.shutdownNow();
    }

    public byte[] encodePushData(int token, byte[] eui, String json) {
        // build UDP packet
        ByteBuffer bb = ByteBuffer.allocate(1500);
        bb.put(PROTOCOL_VERSION);
        bb.putShort((short) (token & 0xFFFF));
        bb.put((byte) 0); // PUSH DATA identifier
        bb.put(eui);
        bb.put(json.getBytes(StandardCharsets.US_ASCII));
        return Arrays.copyOf(bb.array(), bb.position());
    }

    public byte[] encodePullResp(int token, TxPk packet) {
        // encode packet as JSON
        UdpPullRespJson json = new UdpPullRespJson(packet);
        try {
            String jsonData = MAPPER.writeValueAsString(json);

            // build UDP packet
            ByteBuffer bb = ByteBuffer.allocate(1500);
            bb.put(PROTOCOL_VERSION);
            bb.putShort((short) (token & 0xFFFF));
            bb.put((byte) 3); // PULL RESP identifier
            bb.put(jsonData.getBytes(StandardCharsets.US_ASCII));
            return Arrays.copyOf(bb.array(), bb.position());
        } catch (JsonProcessingException e) {
            LOG.warn("Could not build packet", e);
            return new byte[0];
        }
    }

    private void sendUdp(DatagramSocket socket, byte[] data) {
        try {
            DatagramPacket datagram = new DatagramPacket(data, data.length, inetAddress, config.port);
            socket.send(datagram);
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

    @Override
    public void handleUplink(UplinkMessage uplink) {
        // decode EUI
        RxMetadata rxMetadata = uplink.rxMetadata.get(0);
        byte[] eui = parseHex(rxMetadata.gatewayIds.eui);
        RxPk rxPk = udpMessageBuilder.buildRxPk(uplink);
        UdpPushDataJson pushData = new UdpPushDataJson(rxPk);
        try {
            String json = MAPPER.writeValueAsString(pushData);
            byte[] data = encodePushData(token.incrementAndGet(), eui, json);
            sendUdp(data);
        } catch (JsonProcessingException e) {
            LOG.warn("Could not build packet", e);
        }
    }

    @Override
    public void handleDownlink(Instant time, String gateway, GsDownSendData downlink) {
        TxPk packet = udpMessageBuilder.buildTxPk(time, downlink);
        byte[] data = encodePullResp(token.incrementAndGet(), packet);
        sendUdp(data);
    }

    @Override
    public void handleStatus(Instant time, GatewayIds gatewayIds, GatewayStatus gatewayStatus) {
        // not implemented
        byte[] eui = parseHex(gatewayIds.eui);
        Stat stat = udpMessageBuilder.buildStat(time, gatewayStatus);
        UdpPushDataJson pushData = new UdpPushDataJson(stat);
        try {
            String json = MAPPER.writeValueAsString(pushData);
            byte[] data = encodePushData(token.incrementAndGet(), eui, json);
            sendUdp(data);
        } catch (JsonProcessingException e) {
            LOG.warn("Could not build packet", e);
        }
    }

    private void sendUdp(byte[] data) {
        // schedule for transmission
        if (udpSocket != null && data.length > 0) {
            executor.execute(() -> sendUdp(udpSocket, data));
        }
    }
}
