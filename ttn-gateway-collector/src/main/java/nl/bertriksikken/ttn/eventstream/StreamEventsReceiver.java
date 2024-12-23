package nl.bertriksikken.ttn.eventstream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import okio.BufferedSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Subscribes to the event stream of a gateway.<br>
 * See https://www.thethingsindustries.com/docs/reference/api/events/
 */
public final class StreamEventsReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(StreamEventsReceiver.class);

    private static final MediaType MEDIATYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final Duration PING_INTERVAL = Duration.ofSeconds(60);
    private static final Duration RETRY_INTERVAL = Duration.ofSeconds(60);

    private final ObjectMapper mapper = new ObjectMapper();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final String url;
    private final OkHttpClient httpClient;
    private final Map<Request, Call> callMap = new ConcurrentHashMap<>();

    private volatile boolean canceled = false;

    public StreamEventsReceiver(String url) {
        this.url = Objects.requireNonNull(url);
        httpClient = new OkHttpClient().newBuilder().retryOnConnectionFailure(true).pingInterval(PING_INTERVAL)
            .readTimeout(Duration.ZERO).build();
        mapper.findAndRegisterModules();
    }

    public void subscribe(String gatewayId, String apiKey, IEventStreamCallback eventCallback)
        throws JsonProcessingException {
        LOG.info("Subscribing to event stream for '{}'", gatewayId);

        StreamEventsRequest request = new StreamEventsRequest(gatewayId);
        String requestJson = mapper.writeValueAsString(request);
        RequestBody requestBody = RequestBody.create(MEDIATYPE_JSON, requestJson);
        Request httpRequest = new Request.Builder().post(requestBody).url(url).header("Accept", "text/event-stream")
            .header("Authorization", "Bearer " + apiKey).build();
        connect(httpRequest, eventCallback);
    }

    public void stop() {
        canceled = true;
        callMap.values().forEach(Call::cancel);
        executor.shutdownNow();
        LOG.info("Stopped");
    }

    private void connect(Request request, IEventStreamCallback eventCallback) {
        LOG.info("Starting request {}", request);
        Call call = httpClient.newCall(request);
        callMap.put(request, call);
        call.enqueue(new RequestCallback(eventCallback));
    }

    private final class RequestCallback implements Callback {

        private final IEventStreamCallback callback;

        public RequestCallback(IEventStreamCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            LOG.warn("onFailure: call={}", call, e);
            scheduleReconnect(call.request(), RETRY_INTERVAL);
        }

        @Override
        public void onResponse(Call call, Response response) {
            try (ResponseBody body = response.body()) {
                LOG.info("Stream started: {}", response);
                while (!canceled) {
                    processResponse(body.source());
                }
            } catch (IOException e) {
                if (!canceled) {
                    scheduleReconnect(call.request(), RETRY_INTERVAL);
                }
            } finally {
                LOG.info("Stream stopped");
            }
        }

        private void scheduleReconnect(Request request, Duration delay) {
            LOG.warn("Scheduling reconnect in {} ...", delay);
            var unused = executor.schedule(() -> connect(request, callback), delay.toMillis(), TimeUnit.MILLISECONDS);
        }

        private void processResponse(BufferedSource source) throws IOException {
            String line = source.readUtf8Line();
            if ((line != null) && !line.isEmpty()) {
                try {
                    Event.Result result = mapper.readValue(line, Event.Result.class);
                    callback.eventReceived(result.event());
                } catch (JsonProcessingException e) {
                    LOG.warn("Caught exception processing '{}': {}", line, e.getMessage());
                }
            }
        }
    }

    /**
     * Callback for events as they are received over the event stream.
     */
    public interface IEventStreamCallback {
        // notifies of a received event
        void eventReceived(Event event);
    }

}
