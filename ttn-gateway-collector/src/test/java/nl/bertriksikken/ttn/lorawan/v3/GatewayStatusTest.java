package nl.bertriksikken.ttn.lorawan.v3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

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

        Assert.assertNotNull(Instant.parse(status.time));
        Assert.assertFalse(status.versions.isEmpty());
        Assert.assertFalse(status.antennaLocations.isEmpty());
        Assert.assertFalse(status.ip.isEmpty());

        GatewayStatus.Metrics metrics = status.metrics;
        Assert.assertEquals(5, metrics.rxin());
        Assert.assertEquals(1, metrics.rxok());
        Assert.assertEquals(1, metrics.rxfw());
        Assert.assertEquals(66.7, metrics.ackr(), 0.1);
        Assert.assertEquals(0, metrics.txin());
        Assert.assertEquals(0, metrics.txok());
    }

    @Test
    public void testDeserializeEmpty() throws JsonProcessingException {
        GatewayStatus.Location location = MAPPER.readValue("{}", GatewayStatus.Location.class);
        Assert.assertNotNull(location.latitude());
        Assert.assertNotNull(location.longitude());
        Assert.assertEquals(0, location.altitude());
        Assert.assertEquals("", location.source());
    }

}
