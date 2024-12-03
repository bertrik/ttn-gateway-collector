package nl.bertriksikken.ttngatewaycollector.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.bertriksikken.ttn.lorawan.v3.DownlinkMessage;
import nl.bertriksikken.ttn.lorawan.v3.UplinkMessage;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;

public final class ExportEventTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();

    @Test
    public void testToString() {
        UplinkMessage uplink = new UplinkMessage();
        ExportEvent event = ExportEvent.fromUplinkMessage(uplink);
        Assert.assertNotNull(event.toString());
    }

    @Test
    public void testConvertUplinkMessage() throws IOException {
        URL url = getClass().getResource("/UplinkMessage.json");
        UplinkMessage uplinkMessage = OBJECT_MAPPER.readValue(url, UplinkMessage.class);
        ExportEvent.fromUplinkMessage(uplinkMessage);
    }

    @Test
    public void testConvertUplinkMessageJoin() throws IOException {
        URL url = getClass().getResource("/UplinkMessageJoin.json");
        UplinkMessage uplinkMessage = OBJECT_MAPPER.readValue(url, UplinkMessage.class);
        ExportEvent.fromUplinkMessage(uplinkMessage);
    }

    @Test
    public void testConvertDownlinkMessage() throws IOException {
        URL url = getClass().getResource("/DownlinkMessage.json");
        DownlinkMessage downlinkMessage = OBJECT_MAPPER.readValue(url, DownlinkMessage.class);
        ExportEvent.fromDownlinkData(Instant.now(), "gateway", downlinkMessage);
    }


}
