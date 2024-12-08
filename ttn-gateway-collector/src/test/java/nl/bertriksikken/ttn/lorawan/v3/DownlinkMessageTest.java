package nl.bertriksikken.ttn.lorawan.v3;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

public final class DownlinkMessageTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testSerialize() throws IOException {
        URL url = getClass().getResource("/DownlinkMessage.json");
        AbstractMessage message = MAPPER.readValue(url, AbstractMessage.class);
        DownlinkMessage downlinkMessage = (DownlinkMessage) message;

        Assertions.assertEquals(15, downlinkMessage.rawPayload.length);
        DownlinkMessage.Scheduled scheduled = downlinkMessage.scheduled;
        Assertions.assertEquals(125000, scheduled.dataRate.lora().bandWidth());
        Assertions.assertEquals(867900000, scheduled.frequency);
        Assertions.assertEquals(3432044212L, scheduled.timestamp);
        Assertions.assertEquals(16.15, scheduled.downlink.txPower(), 0.001);
        Assertions.assertEquals(282604918452000L, scheduled.concentratorTimestamp);
    }

}
