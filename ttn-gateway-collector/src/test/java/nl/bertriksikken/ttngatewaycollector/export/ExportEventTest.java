package nl.bertriksikken.ttngatewaycollector.export;

import nl.bertriksikken.ttn.lorawan.v3.UplinkMessage;
import org.junit.Assert;
import org.junit.Test;

public final class ExportEventTest {

    @Test
    public void testToString() {
        UplinkMessage uplink = new UplinkMessage();
        ExportEvent event = ExportEvent.fromUplinkMessage(uplink);
        Assert.assertNotNull(event.toString());
    }
}
