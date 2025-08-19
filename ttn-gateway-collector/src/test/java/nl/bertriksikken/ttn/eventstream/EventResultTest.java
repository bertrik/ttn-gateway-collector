package nl.bertriksikken.ttn.eventstream;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.bertriksikken.ttn.lorawan.v3.AbstractMessage;
import nl.bertriksikken.ttn.lorawan.v3.GatewayUplinkMessage;
import nl.bertriksikken.ttn.lorawan.v3.UplinkMessage;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class EventResultTest {

    private final ObjectMapper mapper = new ObjectMapper();

    public EventResultTest() {
        mapper.findAndRegisterModules();
    }

    @Test
    public void testDecodeEvent() throws IOException {
        URL url = getClass().getResource("/GatewayUplinkMessageResult.json");

        // decode top-level event
        Event.Result result = mapper.readValue(url, Event.Result.class);
        Event event = result.event();
        assertNotNull(event);

        // decode data inside event
        AbstractMessage data = event.getData();
        GatewayUplinkMessage gatewayUplinkMessage = (GatewayUplinkMessage) data;
        UplinkMessage uplinkMessage = gatewayUplinkMessage.getMessage();
        assertNotNull(uplinkMessage);
    }

}
