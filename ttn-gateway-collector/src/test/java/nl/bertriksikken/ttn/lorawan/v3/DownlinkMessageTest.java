package nl.bertriksikken.ttn.lorawan.v3;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class DownlinkMessageTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testSerialize() throws IOException {
        URL url = getClass().getResource("/DownlinkMessage.json");
        AbstractMessage message = MAPPER.readValue(url, AbstractMessage.class);
        DownlinkMessage downlinkMessage = (DownlinkMessage) message;

        assertEquals(15, downlinkMessage.rawPayload.length);
        DownlinkMessage.Scheduled scheduled = downlinkMessage.scheduled;
        assertEquals(125000, scheduled.dataRate.lora().bandWidth());
        assertEquals(867900000, scheduled.frequency);
        assertEquals(3432044212L, scheduled.timestamp);
        assertEquals(16.15, scheduled.downlink.txPower(), 0.001);
        assertEquals(282604918452000L, scheduled.concentratorTimestamp);
    }

}
