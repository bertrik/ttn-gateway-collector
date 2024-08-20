package nl.bertriksikken.ttngatewaycollector.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.bertriksikken.ttn.lorawan.v3.DownlinkMessage;
import nl.bertriksikken.ttn.lorawan.v3.EntityIdentifiers;
import nl.bertriksikken.ttn.lorawan.v3.GatewayStatus;
import nl.bertriksikken.ttn.lorawan.v3.UplinkMessage;
import nl.bertriksikken.ttngatewaycollector.IEventProcessor;
import nl.bertriksikken.ttngatewaycollector.udp.UdpMessageBuilder;
import nl.bertriksikken.ttngatewaycollector.udp.UdpPullResp.TxPk;
import nl.bertriksikken.ttngatewaycollector.udp.UdpPushData.RxPk;
import nl.bertriksikken.ttngatewaycollector.udp.UdpPushData.Stat;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MqttSender implements IEventProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MqttSender.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final UdpMessageBuilder udpMessageBuilder = new UdpMessageBuilder();
    private final MqttSenderConfig config;
    private final MqttClient mqttClient;
    private final MqttConnectOptions options = new MqttConnectOptions();

    public MqttSender(MqttSenderConfig config) {
        this.config = Objects.requireNonNull(config);
        try {
            mqttClient = new MqttClient(config.url, MqttClient.generateClientId(), new MemoryPersistence());
        } catch (MqttException e) {
            throw new IllegalStateException(e);
        }
        options.setAutomaticReconnect(true);
    }

    @Override
    public void start() {
        LOG.info("Starting MQTT sender for url '{}', topics '{}'/'{}'/'{}'", config.url, config.uplinkTopic,
            config.downlinkTopic, config.statusTopic);
        try {
            mqttClient.connect(options);
        } catch (MqttException e) {
            LOG.warn("Could not connect MQTT client", e);
        }
    }

    @Override
    public void stop() {
        LOG.info("Stopping MQTT sender");
        executor.shutdownNow();
    }

    private void publish(String topic, String message) {
        if (!topic.isEmpty() && !message.isEmpty()) {
            try {
                byte[] payload = message.getBytes(StandardCharsets.UTF_8);
                mqttClient.publish(topic, payload, config.qos, false);
            } catch (MqttException e) {
                LOG.warn("Failed to send message '{}' to topic '{}'", message, topic, e);
            }
        }
    }

    @Override
    public void handleUplink(UplinkMessage uplink) {
        RxPk rxPacket = udpMessageBuilder.buildRxPk(uplink);
        try {
            String json = mapper.writeValueAsString(rxPacket);
            executor.execute(() -> publish(config.uplinkTopic, json));
        } catch (JsonProcessingException e) {
            LOG.warn("Failed to serialize", e);
        }
    }

    @Override
    public void handleDownlink(Instant time, String gateway, DownlinkMessage downlink) {
        TxPk txPacket = udpMessageBuilder.buildTxPk(time, downlink);
        try {
            String json = mapper.writeValueAsString(txPacket);
            executor.execute(() -> publish(config.downlinkTopic, json));
        } catch (JsonProcessingException e) {
            LOG.warn("Failed to serialize", e);
        }
    }

    @Override
    public void handleStatus(Instant time, EntityIdentifiers.GatewayIdentifiers gatewayIds, GatewayStatus gatewayStatus) {
        Stat stat = udpMessageBuilder.buildStat(time, gatewayStatus);
        try {
            String json = mapper.writeValueAsString(stat);
            executor.execute(() -> publish(config.statusTopic, json));
        } catch (JsonProcessingException e) {
            LOG.warn("Failed to serialize", e);
        }
    }
}
