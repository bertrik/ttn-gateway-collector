package nl.bertriksikken.ttn.message;

import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.ttn.message.GsDownSendData.Scheduled;

public final class GsDownSendDataTest {

    @Test
    public void testSerialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        URL url = getClass().getResource("/GsDownSendData.json");

        GsDownSendData downSendData = mapper.readValue(url, GsDownSendData.class);
        System.out.println(downSendData);
        
        Assert.assertEquals("type.googleapis.com/ttn.lorawan.v3.DownlinkMessage", downSendData.dataType);
        Assert.assertEquals(15, downSendData.rawPayload.length);
        Scheduled scheduled = downSendData.scheduled;
        Assert.assertEquals(125000, scheduled.dataRate.lora.bandWidth);
        Assert.assertEquals(867900000, scheduled.frequency);
        Assert.assertEquals(3432044212L, scheduled.timestamp);
        Assert.assertEquals(16.15, scheduled.downlink.txPower, 0.001);
        Assert.assertEquals(282604918452000L, scheduled.concentratorTimestamp);
    }
    
}
