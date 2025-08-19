package nl.bertriksikken.ttn.lorawan.v3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class GatewayStatusTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testSerialize() throws IOException {
        URL url = getClass().getResource("/GatewayStatus.json");
        AbstractMessage message = MAPPER.readValue(url, AbstractMessage.class);
        GatewayStatus status = (GatewayStatus) message;

        assertNotNull(Instant.parse(status.time));
        assertFalse(status.versions.isEmpty());
        assertFalse(status.antennaLocations.isEmpty());
        assertFalse(status.ip.isEmpty());

        GatewayStatus.Metrics metrics = status.metrics;
        assertEquals(5, metrics.rxin());
        assertEquals(1, metrics.rxok());
        assertEquals(1, metrics.rxfw());
        assertEquals(66.7, metrics.ackr(), 0.1);
        assertEquals(0, metrics.txin());
        assertEquals(0, metrics.txok());
    }

    @Test
    public void testDeserializeEmpty() throws JsonProcessingException {
        GatewayStatus.Location location = MAPPER.readValue("{}", GatewayStatus.Location.class);
        assertNotNull(location.latitude());
        assertNotNull(location.longitude());
        assertEquals(0, location.altitude());
        assertEquals("", location.source());
    }

}
