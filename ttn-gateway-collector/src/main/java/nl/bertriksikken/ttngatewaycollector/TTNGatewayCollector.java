package nl.bertriksikken.ttngatewaycollector;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import nl.bertriksikken.ttn.eventstream.Event;
import nl.bertriksikken.ttn.eventstream.StreamEventsRequest;
import nl.bertriksikken.ttn.message.UplinkMessage;
import nl.bertriksikken.ttngatewaycollector.export.ExportEventWriter;
import nl.bertriksikken.udp.UdpProtocolSender;

public final class TTNGatewayCollector {

    private static final Logger LOG = LoggerFactory.getLogger(TTNGatewayCollector.class);

    private final TTNGatewayCollectorConfig config;
    private final StreamEventsReceiver receiver;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ExportEventWriter eventWriter;
    private final UdpProtocolSender udpSender;
    private final Map<GatewayFrequencyKey, byte[]> lastPacket = new HashMap<>();

    public TTNGatewayCollector(TTNGatewayCollectorConfig config) {
        this.config = config;
        this.receiver = new StreamEventsReceiver(config.url, this::eventReceived);
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
            LOG.info("Adding receiver for '{}'", receiverConfig.gatewayId);
            StreamEventsRequest request = new StreamEventsRequest(receiverConfig.gatewayId);
            receiver.addSubscription(request, receiverConfig.apiKey);
        }
        receiver.start();
        LOG.info("Started");
    }

    private void stop() {
        LOG.info("Stopping");
        receiver.stop();
        udpSender.stop();
        LOG.info("Stopped");
    }

    // package-private for testing
    void eventReceived(Event event) {
        try {
            // only interested in gateway uplink events
            if (event.getName().equals("gs.up.receive")) {
                LOG.info("Gateway uplink received: {}", event.getData());
                // parse as uplink message
                UplinkMessage uplinkMessage = mapper.treeToValue(event.getData(), UplinkMessage.class);
                String gatewayEui = event.getIdentifiers().at("/0/gateway_ids/eui").asText("");

                // detect and ignore duplicates
                GatewayFrequencyKey key = new GatewayFrequencyKey(gatewayEui, uplinkMessage.settings.frequency);
                byte[] lastData = lastPacket.put(key, uplinkMessage.rawPayload);
                if (Arrays.equals(lastData, uplinkMessage.rawPayload)) {
                    return;
                }

                // send to logger
                String gatewayId = event.getIdentifiers().at("/0/gateway_ids/gateway_id").asText("unknown");
                eventWriter.write(gatewayId, uplinkMessage);

                // send to UDP sender
                udpSender.send(gatewayEui, uplinkMessage);
            }
        } catch (IOException e) {
            LOG.warn("Exception processing event", e);
        }
    }

    private static TTNGatewayCollectorConfig readConfig(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
        try {
            return mapper.readValue(file, TTNGatewayCollectorConfig.class);
        } catch (IOException e) {
            LOG.warn("Failed to load config {}, writing defaults", file.getAbsoluteFile());
            TTNGatewayCollectorConfig config = new TTNGatewayCollectorConfig();
            mapper.writeValue(file, config);
            return config;
        }
    }
}
