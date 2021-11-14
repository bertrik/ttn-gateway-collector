package nl.bertriksikken.ttn.message;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class UplinkMessageTest {

    private final ObjectMapper mapper = new ObjectMapper();
    
    public UplinkMessageTest() {
        mapper.findAndRegisterModules();
    }
    
    @Test
    public void testUnconfirmed() throws IOException, URISyntaxException {
        URL url = getClass().getResource("/unconfirmed.json");
        
        UplinkMessage uplink = mapper.readValue(url, UplinkMessage.class);
        System.out.println(uplink);
    }
    
    @Test
    public void testJoin() throws IOException, URISyntaxException {
        URL url = getClass().getResource("/join.json");
        
        UplinkMessage uplink = mapper.readValue(url, UplinkMessage.class);
        System.out.println(uplink);
    }
    
}
