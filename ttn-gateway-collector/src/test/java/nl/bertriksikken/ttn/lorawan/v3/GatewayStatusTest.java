package nl.bertriksikken.ttn.lorawan.v3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;

public final class GatewayStatusTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testSerialize() throws IOException {
        URL url = getClass().getResource("/GatewayStatus.json");
        AbstractMessage message = MAPPER.readValue(url, AbstractMessage.class);
        GatewayStatus status = (GatewayStatus) message;

        Assertions.assertNotNull(Instant.parse(status.time));
        Assertions.assertFalse(status.versions.isEmpty());
        Assertions.assertFalse(status.antennaLocations.isEmpty());
        Assertions.assertFalse(status.ip.isEmpty());

        GatewayStatus.Metrics metrics = status.metrics;
        Assertions.assertEquals(5, metrics.rxin());
        Assertions.assertEquals(1, metrics.rxok());
        Assertions.assertEquals(1, metrics.rxfw());
        Assertions.assertEquals(66.7, metrics.ackr(), 0.1);
        Assertions.assertEquals(0, metrics.txin());
        Assertions.assertEquals(0, metrics.txok());
    }

    @Test
    public void testDeserializeEmpty() throws JsonProcessingException {
        GatewayStatus.Location location = MAPPER.readValue("{}", GatewayStatus.Location.class);
        Assertions.assertNotNull(location.latitude());
        Assertions.assertNotNull(location.longitude());
        Assertions.assertEquals(0, location.altitude());
        Assertions.assertEquals("", location.source());
    }

}
