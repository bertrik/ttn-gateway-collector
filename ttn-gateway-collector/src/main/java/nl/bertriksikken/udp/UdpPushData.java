package nl.bertriksikken.udp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Semtech UDP protocol PUSH DATA packet, see
 * https://github.com/Lora-net/packet_forwarder/blob/master/PROTOCOL.TXT#L92
 */
public final class UdpPushData {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final byte PROTOCOL_VERSION = 2;

    private final int token;
    private final byte[] eui;
    private final UdpPushDataJson json;

    public UdpPushData(byte[] eui, int token) {
        this.token = token;
        this.eui = eui.clone();
        this.json = new UdpPushDataJson();
    }

    public void addPacket(Instant time, long timestamp, double frequency, String dataRate, String codingRate, int rssi,
        double snr, byte[] data) {
        json.addReceiveData(time, timestamp, frequency, dataRate, codingRate, rssi, snr, data);
    }

    public byte[] encode() throws IOException {
        // encode packet as JSON
        String jsonData = MAPPER.writeValueAsString(json);

        // build packet
        ByteBuffer bb = ByteBuffer.allocate(1500);
        bb.put(PROTOCOL_VERSION);
        bb.putShort((short) (token & 0xFFFF));
        bb.put((byte) 0); // PUSH DATA identifier
        bb.put(eui);
        bb.put(jsonData.getBytes(StandardCharsets.US_ASCII));
        return Arrays.copyOf(bb.array(), bb.position());
    }

}
