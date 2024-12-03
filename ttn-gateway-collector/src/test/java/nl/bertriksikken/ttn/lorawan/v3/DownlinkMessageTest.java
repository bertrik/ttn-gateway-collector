package nl.bertriksikken.ttn.lorawan.v3;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

public final class DownlinkMessageTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testSerialize() throws IOException {
        URL url = getClass().getResource("/DownlinkMessage.json");
        AbstractMessage message = MAPPER.readValue(url, AbstractMessage.class);
        DownlinkMessage downlinkMessage = (DownlinkMessage) message;

        Assert.assertEquals(15, downlinkMessage.rawPayload.length);
        DownlinkMessage.Scheduled scheduled = downlinkMessage.scheduled;
        Assert.assertEquals(125000, scheduled.dataRate.lora().bandWidth());
        Assert.assertEquals(867900000, scheduled.frequency);
        Assert.assertEquals(3432044212L, scheduled.timestamp);
        Assert.assertEquals(16.15, scheduled.downlink.txPower(), 0.001);
        Assert.assertEquals(282604918452000L, scheduled.concentratorTimestamp);
    }

}
