package nl.bertriksikken.ttngatewaycollector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import nl.bertriksikken.ttn.eventstream.Event;
import nl.bertriksikken.ttn.eventstream.StreamEventsReceiver;
import nl.bertriksikken.ttn.lorawan.v3.DownlinkMessage;
import nl.bertriksikken.ttn.lorawan.v3.GatewayStatus;
import nl.bertriksikken.ttn.lorawan.v3.GatewayUplinkMessage;
import nl.bertriksikken.ttn.lorawan.v3.UplinkMessage;
import nl.bertriksikken.ttngatewaycollector.export.ExportEventWriter;
import nl.bertriksikken.ttngatewaycollector.mqtt.MqttSender;
import nl.bertriksikken.ttngatewaycollector.udp.UdpSender;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class TTNGatewayCollector {

    private static final Logger LOG = LoggerFactory.getLogger(TTNGatewayCollector.class);

    private final List<IEventProcessor> eventProcessors = new ArrayList<>();
    private final TTNGatewayCollectorConfig config;
    private final StreamEventsReceiver receiver;

    public TTNGatewayCollector(TTNGatewayCollectorConfig config) {
        this.config = config;

        receiver = new StreamEventsReceiver(config.url);
        if (!config.csvLoggerConfig.logFileName.isEmpty()) {
            eventProcessors.add(new ExportEventWriter(config.csvLoggerConfig));
        }
        if (!config.udpProtocolConfig.host.isEmpty()) {
            eventProcessors.add(new UdpSender(config.udpProtocolConfig));
        }
        if (!config.mqttSenderConfig.url.isEmpty()) {
            eventProcessors.add(new MqttSender(config.mqttSenderConfig));
        }
    }

    public static void main(String[] args) throws IOException {
        PropertyConfigurator.configure("log4j.properties");

        TTNGatewayCollectorConfig config = readConfig(new File("ttn-gateway-collector.yaml"));
        TTNGatewayCollector collector = new TTNGatewayCollector(config);
        collector.start();
        Runtime.getRuntime().addShutdownHook(new Thread(collector::stop));
    }

    private void start() throws IOException {
        LOG.info("Starting");
        for (IEventProcessor processor : eventProcessors) {
            processor.start();
        }
        for (GatewayReceiverConfig receiverConfig : config.receivers) {
            String gatewayId = receiverConfig.gatewayId;
            receiver.subscribe(gatewayId, receiverConfig.apiKey, event -> eventReceived(gatewayId, event));
        }
    }

    private void stop() {
        receiver.stop();
        eventProcessors.forEach(IEventProcessor::stop);
        LOG.info("Stopped");
    }

    // package-private for testing
    void eventReceived(String gatewayId, Event event) {
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
                if (event.getData() instanceof DownlinkMessage downlinkMessage) {
                    eventProcessors.forEach(p -> p.handleDownlink(event.getTime(), gatewayId, downlinkMessage));
                }
                break;
            case "gs.down.tx.success":
                break;
            case "gs.gateway.connection.stats":
                // ignore
                break;
            case "gs.status.receive":
                LOG.info("Gateway status received: {}", event.getData());
                if (event.getData() instanceof GatewayStatus gatewayStatus) {
                    eventProcessors.forEach(p -> p.handleStatus(event.getTime(), event.getGatewayIds(), gatewayStatus));
                }
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
                if (event.getData() instanceof GatewayUplinkMessage gatewayUplinkMessage) {
                    UplinkMessage uplinkMessage = gatewayUplinkMessage.getMessage();
                    eventProcessors.forEach(p -> p.handleUplink(uplinkMessage));
                } else {
                    LOG.warn("Unhandled gs.up.receive: {}", event.getData());
                }
                break;
            default:
                LOG.info("Unhandled event {}: {}", event.getName(), event.getData());
                break;
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
