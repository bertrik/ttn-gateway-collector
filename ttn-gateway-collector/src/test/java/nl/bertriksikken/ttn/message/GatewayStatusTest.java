package nl.bertriksikken.ttn.message;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.ttn.message.GatewayStatus.Metrics;

public final class GatewayStatusTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testSerialize() throws IOException {
        URL url = getClass().getResource("/GatewayStatus.json");
        GatewayStatus status = MAPPER.readValue(url, GatewayStatus.class);
        Assert.assertEquals("type.googleapis.com/ttn.lorawan.v3.GatewayStatus", status.type);
        Assert.assertNotNull(Instant.parse(status.time));
        Assert.assertFalse(status.versions.values().isEmpty());
        Assert.assertFalse(status.antennaLocations.isEmpty());
        Assert.assertFalse(status.ip.isEmpty());

        Metrics metrics = status.metrics;
        Assert.assertEquals(5, metrics.rxin);
        Assert.assertEquals(1, metrics.rxok);
        Assert.assertEquals(1, metrics.rxfw);
        Assert.assertEquals(66.7, metrics.ackr, 0.1);
        Assert.assertEquals(0, metrics.txin);
        Assert.assertEquals(0, metrics.txok);
    }

}
