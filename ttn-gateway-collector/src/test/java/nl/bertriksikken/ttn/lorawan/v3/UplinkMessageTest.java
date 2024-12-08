package nl.bertriksikken.ttn.lorawan.v3;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

public final class UplinkMessageTest {

    private final ObjectMapper mapper = new ObjectMapper();
    
    public UplinkMessageTest() {
        mapper.findAndRegisterModules();
    }
    
    @Test
    public void testUnconfirmed() throws IOException {
        URL url = getClass().getResource("/UplinkMessage.json");

        UplinkMessage uplink = mapper.readValue(url, UplinkMessage.class);
        System.out.println(uplink);
        Assertions.assertEquals(3119016580L, uplink.rxMetadata.get(0).timestamp);
    }
    
    @Test
    public void testJoin() throws IOException {
        URL url = getClass().getResource("/UplinkMessageJoin.json");
        
        UplinkMessage uplink = mapper.readValue(url, UplinkMessage.class);
        System.out.println(uplink);
    }
    
}
