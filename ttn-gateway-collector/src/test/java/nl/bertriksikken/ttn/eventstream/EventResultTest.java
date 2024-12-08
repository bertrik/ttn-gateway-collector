package nl.bertriksikken.ttn.eventstream;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.bertriksikken.ttn.lorawan.v3.AbstractMessage;
import nl.bertriksikken.ttn.lorawan.v3.GatewayUplinkMessage;
import nl.bertriksikken.ttn.lorawan.v3.UplinkMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

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
        Assertions.assertNotNull(event);

        // decode data inside event
        AbstractMessage data = event.getData();
        GatewayUplinkMessage gatewayUplinkMessage = (GatewayUplinkMessage) data;
        UplinkMessage uplinkMessage = gatewayUplinkMessage.getMessage();
        Assertions.assertNotNull(uplinkMessage);
    }

}
