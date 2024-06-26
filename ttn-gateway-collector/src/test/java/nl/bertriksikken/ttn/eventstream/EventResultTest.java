package nl.bertriksikken.ttn.eventstream;

import java.io.IOException;
import java.net.URL;

import nl.bertriksikken.ttn.lorawan.v3.AbstractMessage;
import nl.bertriksikken.ttn.lorawan.v3.GatewayUplinkMessage;
import nl.bertriksikken.ttn.lorawan.v3.UplinkMessage;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class EventResultTest {
    
    private final ObjectMapper mapper = new ObjectMapper();
    
    public EventResultTest() {
        mapper.findAndRegisterModules();
    }

    @Test
    public void testDecodeEvent() throws IOException {
        URL url = getClass().getResource("/GatewayUplinkMessageResult.json");

        // decode top-level event
        EventResult result = mapper.readValue(url, EventResult.class);
        Event event = result.getEvent();
        Assert.assertNotNull(event);
        
        // decode data inside event
        AbstractMessage data = event.getData();
        GatewayUplinkMessage gatewayUplinkMessage = (GatewayUplinkMessage) data;
        UplinkMessage uplinkMessage = gatewayUplinkMessage.getMessage();
        Assert.assertNotNull(uplinkMessage);
    }

}
