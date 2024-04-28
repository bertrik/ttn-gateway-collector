package nl.bertriksikken.ttngatewaycollector;

import nl.bertriksikken.ttn.lorawan.v3.DownlinkMessage;
import nl.bertriksikken.ttn.lorawan.v3.EntityIdentifiers;
import nl.bertriksikken.ttn.lorawan.v3.GatewayStatus;
import nl.bertriksikken.ttn.lorawan.v3.UplinkMessage;

import java.io.IOException;
import java.time.Instant;

public interface IEventProcessor {

    void handleUplink(UplinkMessage uplink);

    void handleDownlink(Instant time, String gateway, DownlinkMessage downlink);

    void handleStatus(Instant time, EntityIdentifiers.GatewayIdentifiers gatewayIds, GatewayStatus gatewayStatus);

    void start() throws IOException;

    void stop();

}
