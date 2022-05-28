package nl.bertriksikken.ttngatewaycollector.export;

import org.junit.Assert;
import org.junit.Test;

import nl.bertriksikken.ttn.message.UplinkMessage;

public final class ExportEventTest {

    @Test
    public void testToString() {
        UplinkMessage uplink = new UplinkMessage();
        ExportEvent event = ExportEvent.fromUplinkMessage(uplink);
        Assert.assertNotNull(event.toString());
    }
}
