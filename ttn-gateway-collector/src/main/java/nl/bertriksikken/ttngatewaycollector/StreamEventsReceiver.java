package nl.bertriksikken.ttngatewaycollector;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.ttn.eventstream.EventResult;
import nl.bertriksikken.ttn.eventstream.StreamEventsRequest;
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

    private final ObjectMapper MAPPER = new ObjectMapper();
    private final RequestCallback requestCallback = new RequestCallback();

    private final String url;
    private final IEventStreamCallback callback;
    private final List<Call> calls = new ArrayList<>();

    private volatile boolean canceled = false;

    public StreamEventsReceiver(String url, IEventStreamCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    public void subscribe(StreamEventsRequest request, String apiKey) throws JsonProcessingException {
        OkHttpClient httpClient = new OkHttpClient().newBuilder()
                .retryOnConnectionFailure(true)
                .pingInterval(PING_INTERVAL)
                .readTimeout(Duration.ZERO).build();
        String requestJson = MAPPER.writeValueAsString(request);
        RequestBody body = RequestBody.create(MEDIATYPE_JSON, requestJson);
        Request httpRequest = new Request.Builder()
                .post(body)
                .url(url)
                .header("Accept", "text/event-stream")
                .header("Authorization", "Bearer " + apiKey).build();
        calls.add(httpClient.newCall(httpRequest));
    }
    
    public void start() {
        LOG.info("Starting");
        calls.forEach(c -> c.enqueue(requestCallback));
    }

    public void stop() {
        LOG.info("Stopping");
        canceled = true;
        calls.forEach(c -> c.cancel());
    }

    private final class RequestCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException e) {
            LOG.info("onFailure: call={}, exception={}", call, e);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            try (ResponseBody body = response.body()) {
                LOG.info("Stream started: {}", response);
                while (!canceled) {
                    processResponse(body.source());
                }
            } finally {
                LOG.info("Stream stopped");
            }
        }
        
        private void processResponse(BufferedSource source) throws IOException {
            // read line
            String line = "";
            line = source.readUtf8Line();

            // process line
            if (!line.isEmpty()) {
                EventResult result = MAPPER.readValue(line, EventResult.class);
                callback.eventReceived(result.getEvent());
            }
        }
    }
    
}
