package nl.bertriksikken.ttngatewaycollector.udp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UdpSenderConfigTest {

    @Test
    public void testDefaults() throws JsonProcessingException {
        String json = "{}";
        ObjectMapper mapper = new ObjectMapper();
        UdpSenderConfig config = mapper.readValue(json, UdpSenderConfig.class);
        Assertions.assertTrue(config.host.isEmpty());
        Assertions.assertEquals(1700, config.port);
    }
}
