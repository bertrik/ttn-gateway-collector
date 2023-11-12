package nl.bertriksikken.ttngatewaycollector;

import java.io.IOException;
import java.time.Instant;

import nl.bertriksikken.ttn.message.GatewayIdentifier.GatewayIds;
import nl.bertriksikken.ttn.message.GatewayStatus;
import nl.bertriksikken.ttn.message.GsDownSendData;
import nl.bertriksikken.ttn.message.UplinkMessage;

public interface IEventProcessor {

    void handleUplink(UplinkMessage uplink);

    void handleDownlink(Instant time, String gateway, GsDownSendData downlink);

    void handleStatus(Instant time, GatewayIds gatewayIds, GatewayStatus gatewayStatus);

    void start() throws IOException;

    void stop();

}
