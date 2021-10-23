package nl.bertriksikken.ttn.eventstream;

import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.ttn.message.UplinkMessage;

public final class EventResultTest {
    
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testDecodeEvent() throws IOException {
        URL url = getClass().getResource("/unconfirmed_up_event.json");

        // decode top-level event
        EventResult result = mapper.readValue(url, EventResult.class);
        Event event = result.getEvent();
        Assert.assertNotNull(event);
        
        // decode data inside event
        UplinkMessage uplinkMessage = mapper.treeToValue(event.getData(), UplinkMessage.class);
        Assert.assertNotNull(uplinkMessage);
    }

}
