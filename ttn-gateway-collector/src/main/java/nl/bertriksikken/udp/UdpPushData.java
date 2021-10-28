package nl.bertriksikken.udp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class UdpPushData {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final byte PROTOCOL_VERSION = 2;

    private final Random random;
    private final byte[] eui;
    private final UdpPushDataJson json;

    public UdpPushData(byte[] eui) {
        this.random = new Random(Instant.now().toEpochMilli());
        this.eui = eui.clone();
        this.json = new UdpPushDataJson();
    }

    public void addPacket(Instant time, double frequency, String dataRate, String codingRate, int rssi, double snr,
        byte[] data) {
        json.addReceiveData(time, frequency, dataRate, codingRate, rssi, snr, data);
    }

    public byte[] encode() throws IOException {
        // encode packet as JSON
        String jsonData = MAPPER.writeValueAsString(json);

        int token = random.nextInt();

        // build packet
        ByteBuffer bb = ByteBuffer.allocate(1500);
        bb.put(PROTOCOL_VERSION);
        bb.putShort((short) token);
        bb.put((byte) 0);
        bb.put(eui);
        bb.put(jsonData.getBytes(StandardCharsets.US_ASCII));
        return Arrays.copyOf(bb.array(), bb.position());
    }

}
