package nl.bertriksikken.ttngatewaycollector;

import java.io.IOException;
import java.time.Duration;

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

    private static final Duration PING_INTERVAL = Duration.ofSeconds(60);

    // we share one object mapper between all receivers
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final MediaType MEDIATYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private final IEventStreamCallback callback;
    private final Call call;

    private volatile boolean canceled = false;

    public StreamEventsReceiver(String url, StreamEventsRequest request, String apiKey, IEventStreamCallback callback)
            throws JsonProcessingException {
        // create call
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
        call = httpClient.newCall(httpRequest);

        this.callback = callback;
    }

    public void start() {
        Callback callback = new MyCallback();
        call.enqueue(callback);
    }

    public void stop() {
        canceled = true;
    }

    private final class MyCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException e) {
            LOG.info("onFailure: call={}, exception={}", call, e);
        }

        @Override
        public void onResponse(Call call, Response response) {
            LOG.info("onResponse: call={}, response={}", call, response);
            try (ResponseBody body = response.body()) {
                while (!canceled) {
                    processResponse(body.source());
                }
            }
        }
        
        private void processResponse(BufferedSource source) {
            // read line
            String line = "";
            try {
                line = source.readUtf8Line();
            } catch (IOException e1) {
                LOG.warn("Error reading line from response");
                return;
            }

            // process line
            try {
                if (!line.isEmpty()) {
                    EventResult result = MAPPER.readValue(line, EventResult.class);
                    callback.eventReceived(result.getEvent());
                }
            } catch (IOException e) {
                LOG.warn("Error decoding line: {}", line);
            }
        }
    }
    
}
