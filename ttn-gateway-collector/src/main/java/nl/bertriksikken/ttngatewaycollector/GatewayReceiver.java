package nl.bertriksikken.ttngatewaycollector;

import java.io.IOException;
import java.time.Duration;

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

/**
 * Subscribes to the event stream of a gateway.<br>
 * See https://www.thethingsindustries.com/docs/reference/api/events/
 */
public final class GatewayReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayReceiver.class);

    // we share one object mapper between all receivers
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final MediaType MEDIATYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private Call call;
    private volatile boolean canceled;
    
    public GatewayReceiver(String url, GatewayReceiverConfig config) throws JsonProcessingException {
        // create client
        OkHttpClient httpClient = new OkHttpClient().newBuilder().retryOnConnectionFailure(true)
                .readTimeout(Duration.ZERO).build();

        // create request
        GatewaySubscriptionMessage message = new GatewaySubscriptionMessage(config.gatewayId);
        String request = MAPPER.writeValueAsString(message);
        RequestBody body = RequestBody.create(MEDIATYPE_JSON, request);
        Request httpRequest = new Request.Builder()
                .post(body)
                .url(url)
                .header("Accept", "text/event-stream")
                .header("Authorization", "Bearer " + config.apiKey).build();
        call = httpClient.newCall(httpRequest);
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
        public void onResponse(Call call, Response response) throws IOException {
            LOG.info("onResponse: call={}, response={}", call, response);
            try {
                ResponseBody body = response.body();
                while (!canceled) {
                    String line = body.source().readUtf8Line();
                    LOG.info("line: {}", line);
                }
            } finally {
                response.close();
            }
        }
    }
    
}
