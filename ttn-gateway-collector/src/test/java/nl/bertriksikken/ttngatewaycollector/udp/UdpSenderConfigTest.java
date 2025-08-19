package nl.bertriksikken.ttngatewaycollector.udp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UdpSenderConfigTest {

    @Test
    public void testDefaults() throws JsonProcessingException {
        String json = "{}";
        ObjectMapper mapper = new ObjectMapper();
        UdpSenderConfig config = mapper.readValue(json, UdpSenderConfig.class);
        assertTrue(config.host.isEmpty());
        assertEquals(1700, config.port);
    }
}
