package nl.bertriksikken.ttngatewaycollector;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import nl.bertriksikken.ttn.eventstream.Event;
import nl.bertriksikken.ttn.eventstream.StreamEventsReceiver;
import nl.bertriksikken.ttn.message.GsDownSendData;
import nl.bertriksikken.ttn.message.GsUpReceiveData;
import nl.bertriksikken.ttn.message.UplinkMessage;
import nl.bertriksikken.ttngatewaycollector.export.ExportEvent;
import nl.bertriksikken.ttngatewaycollector.export.ExportEventWriter;
import nl.bertriksikken.udp.UdpProtocolSender;

public final class TTNGatewayCollector {

    private static final Logger LOG = LoggerFactory.getLogger(TTNGatewayCollector.class);

    private final TTNGatewayCollectorConfig config;
    private final StreamEventsReceiver receiver;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ExportEventWriter eventWriter;
    private final UdpProtocolSender udpSender;

    public TTNGatewayCollector(TTNGatewayCollectorConfig config) {
        this.config = config;
        this.receiver = new StreamEventsReceiver(config.url);
        this.eventWriter = new ExportEventWriter(new File(config.logFileName));
        this.udpSender = new UdpProtocolSender(config.udpProtocolConfig);

        mapper.findAndRegisterModules();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        PropertyConfigurator.configure("log4j.properties");

        TTNGatewayCollectorConfig config = readConfig(new File("ttn-gateway-collector.yaml"));
        TTNGatewayCollector collector = new TTNGatewayCollector(config);
        collector.start();
        Runtime.getRuntime().addShutdownHook(new Thread(collector::stop));
    }

    private void start() throws IOException {
        LOG.info("Starting");
        udpSender.start();
        for (GatewayReceiverConfig receiverConfig : config.receivers) {
            String gatewayId = receiverConfig.gatewayId;
            receiver.subscribe(gatewayId, receiverConfig.apiKey, event -> eventReceived(gatewayId, event));
        }
        LOG.info("Started");
    }

    private void stop() {
        LOG.info("Stopping");
        receiver.stop();
        udpSender.stop();
        LOG.info("Stopped");
    }

    // package-private for testing
    void eventReceived(String gatewayId, Event event) {
        ExportEvent exportEvent;
        try {
            // only interested in gateway uplink events
            switch (event.getName()) {
            case "events.stream.start":
                LOG.info("Stream started");
                break;
            case "gs.down.schedule.attempt":
                // ignore
                break;
            case "gs.down.send":
                LOG.info("Gateway downlink received: {}", event.getData());
                GsDownSendData gsDownSendData = mapper.treeToValue(event.getData(), GsDownSendData.class);
                exportEvent = ExportEvent.fromDownlinkData(event.getTime(), gatewayId, gsDownSendData);
                eventWriter.write(exportEvent);
                break;
            case "gs.down.tx.success":
                break;
            case "gs.gateway.connection.stats":
                // ignore
                break;
            case "gs.txack.receive":
            case "gs.txack.forward":
                // ignore
                break;
            case "gs.up.drop":
            case "gs.up.forward":
                // ignore
                break;
            case "gs.up.receive":
                LOG.info("Gateway uplink received: {}", event.getData());
                // parse as gs.up.receive data
                GsUpReceiveData gsUpReceiveData = mapper.treeToValue(event.getData(), GsUpReceiveData.class);
                if (gsUpReceiveData.getDataType().equals("type.googleapis.com/ttn.lorawan.v3.GatewayUplinkMessage")) {
                    // parse as uplink message
                    UplinkMessage uplinkMessage = mapper.treeToValue(gsUpReceiveData.getMessage(), UplinkMessage.class);

                    // send to logger
                    exportEvent = ExportEvent.fromUplinkMessage(uplinkMessage);
                    eventWriter.write(exportEvent);

                    // send to UDP sender
                    udpSender.send(uplinkMessage);
                } else {
                    LOG.warn("Unhandled gs.up.receive: {}", gsUpReceiveData.getMessage());
                }
                break;
            default:
                LOG.info("Unhandled event {}: {}", event.getName(), event.getData());
                break;
            }
        } catch (IOException e) {
            LOG.warn("Exception processing event", e);
        }
    }

    private static TTNGatewayCollectorConfig readConfig(File file) throws IOException {
        TTNGatewayCollectorConfig config = new TTNGatewayCollectorConfig();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        if (file.exists()) {
            try {
                return mapper.readValue(file, TTNGatewayCollectorConfig.class);
            } catch (IOException e) {
                LOG.warn("Failed to load config {}, using defaults", file.getAbsoluteFile());
            }
        } else {
            LOG.warn("No config found, writing default configuration {}", file.getAbsoluteFile());
            mapper.writeValue(file, config);
        }
        return config;
    }
}
