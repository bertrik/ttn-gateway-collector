package nl.bertriksikken.ttn.eventstream;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

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
    private final RequestCallback requestCallback = new RequestCallback();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final String url;
    private final IEventStreamCallback callback;
    private final OkHttpClient httpClient;
    private final List<Request> requests = new ArrayList<>();
    private final Map<Request, Call> requestMap = new ConcurrentHashMap<>();

    private volatile boolean canceled = false;

    public StreamEventsReceiver(String url, IEventStreamCallback callback) {
        this.url = url;
        this.callback = callback;
        httpClient = new OkHttpClient().newBuilder().retryOnConnectionFailure(true).pingInterval(PING_INTERVAL)
            .readTimeout(Duration.ZERO).build();
        mapper.findAndRegisterModules();
    }

    public void addSubscription(StreamEventsRequest request, String apiKey) throws JsonProcessingException {
        String requestJson = mapper.writeValueAsString(request);
        RequestBody body = RequestBody.create(MEDIATYPE_JSON, requestJson);
        Request httpRequest = new Request.Builder().post(body).url(url).header("Accept", "text/event-stream")
            .header("Authorization", "Bearer " + apiKey).build();
        requests.add(httpRequest);
    }

    public void start() {
        requests.forEach(this::connect);
    }

    public void stop() {
        LOG.info("Stopping");
        canceled = true;
        executor.shutdownNow();
        requestMap.values().forEach(Call::cancel);
    }

    private void connect(Request request) {
        LOG.info("Starting request {}", request);
        Call call = httpClient.newCall(request);
        requestMap.put(request, call);
        call.enqueue(requestCallback);
    }

    private final class RequestCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException e) {
            LOG.warn("onFailure: call={}, exception={}", call, e);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            try (ResponseBody body = response.body()) {
                LOG.info("Stream started: {}", response);
                while (!canceled) {
                    processResponse(body.source());
                }
            } catch (IOException e) {
                if (!canceled) {
                    LOG.warn("Scheduling reconnect in {} ...", RETRY_INTERVAL, e);
                    executor.schedule(() -> connect(call.request()), RETRY_INTERVAL.toMillis(), TimeUnit.MILLISECONDS);
                }
            } finally {
                LOG.info("Stream stopped");
            }
        }

        private void processResponse(BufferedSource source) throws IOException {
            String line = source.readUtf8Line();
            if ((line != null) && !line.isEmpty()) {
                EventResult result = mapper.readValue(line, EventResult.class);
                callback.eventReceived(result.getEvent());
            }
        }
    }

    /**
     * Callback for events as they are received over the event stream.
     */
    public interface IEventStreamCallback {
        // notifies of a received event
        public void eventReceived(Event event);
    }

}
