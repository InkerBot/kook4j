package bot.inker.kook4j.ws;

import bot.inker.kook4j.BotInstance;
import bot.inker.kook4j.Kook4jCodec;
import bot.inker.kook4j.exception.KookSessionExpiredException;
import bot.inker.kook4j.exception.KookTokenExpiredException;
import bot.inker.kook4j.exception.KookWebSocketException;
import bot.inker.kook4j.http.HttpClient;
import com.google.gson.JsonObject;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;

public final class KookWebSocket extends WebSocketListener {

    private static final Logger log = LoggerFactory.getLogger(KookWebSocket.class);
    private static final int HEARTBEAT_INTERVAL_MS = 30_000;
    private static final int HELLO_TIMEOUT_MS = 6_000;
    private static final int MAX_RETRY_DELAY_MS = 60_000;

    private final BotInstance bot;
    private final HttpClient httpClient;
    private final boolean compress;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        var t = new Thread(r, "kook4j-heartbeat");
        t.setDaemon(true);
        return t;
    });
    private volatile WebSocket webSocket;
    private volatile String sessionId;
    private volatile int lastSn;
    private volatile boolean active;
    private ScheduledFuture<?> heartbeatFuture;
    private ScheduledFuture<?> pongTimeoutFuture;
    private int retryCount;

    public KookWebSocket(BotInstance bot, HttpClient httpClient, boolean compress) {
        this.bot = bot;
        this.httpClient = httpClient;
        this.compress = compress;
    }

    public void connect() {
        active = true;
        retryCount = 0;
        doConnect(false);
    }

    public void disconnect() {
        active = false;
        stopHeartbeat();
        if (webSocket != null) {
            webSocket.close(1000, "Client disconnect");
            webSocket = null;
        }
        scheduler.shutdownNow();
    }

    private void doConnect(boolean resume) {
        if (!active) return;

        try {
            var url = httpClient.getGatewayUrl(compress);
            if (resume && sessionId != null) {
                url += "&resume=1&sn=" + lastSn + "&session_id=" + sessionId;
            }

            log.info("Connecting to Kook gateway{}...", resume ? " (resume)" : "");
            var request = new Request.Builder().url(url).build();
            webSocket = httpClient.okHttpClient().newWebSocket(request, this);
        } catch (Exception e) {
            log.error("Failed to get gateway URL", e);
            scheduleReconnect();
        }
    }

    @Override
    public void onOpen(WebSocket ws, Response response) {
        log.debug("WebSocket opened");
    }

    @Override
    public void onMessage(WebSocket ws, String text) {
        handleMessage(text);
    }

    @Override
    public void onMessage(WebSocket ws, okio.ByteString bytes) {
        if (compress) {
            try {
                handleMessage(decompress(bytes.toByteArray()));
            } catch (Exception e) {
                log.error("Failed to decompress WebSocket message", new KookWebSocketException("Decompression failed", e));
            }
        } else {
            handleMessage(bytes.utf8());
        }
    }

    @Override
    public void onFailure(WebSocket ws, Throwable t, Response response) {
        log.error("WebSocket failure", t);
        stopHeartbeat();
        if (active) {
            scheduleReconnect();
        }
    }

    @Override
    public void onClosing(WebSocket ws, int code, String reason) {
        log.info("WebSocket closing: {} {}", code, reason);
        ws.close(code, reason);
    }

    @Override
    public void onClosed(WebSocket ws, int code, String reason) {
        log.info("WebSocket closed: {} {}", code, reason);
        stopHeartbeat();
        if (active) {
            scheduleReconnect();
        }
    }

    private void handleMessage(String json) {
        try {
            var obj = Kook4jCodec.parseObject(json);
            var signal = Signal.of(obj.get("s").getAsInt());

            switch (signal) {
                case HELLO -> handleHello(obj);
                case EVENT -> handleEvent(obj);
                case PONG -> handlePong();
                case RECONNECT -> handleReconnect(obj);
                case RESUME_ACK -> log.info("Session resumed successfully");
                default -> log.debug("Received signal: {}", signal);
            }
        } catch (Exception e) {
            log.error("Error handling message: {}", json, e);
        }
    }

    private void handleHello(JsonObject obj) {
        var data = obj.getAsJsonObject("d");
        var code = data.get("code").getAsInt();

        if (code != 0) {
            // Map documented HELLO error codes to descriptive exceptions for logging.
            // 40100 – missing params, 40101 – invalid token, 40102 – token verification failed,
            // 40103 – token expired (reconnect required).
            Exception cause = switch (code) {
                case 40103 -> new KookTokenExpiredException(code, "Token expired");
                default -> new KookWebSocketException("WebSocket HELLO failed (code=" + code + ")");
            };
            log.error("WebSocket HELLO failed", cause);
            webSocket.close(1000, "Hello failed");
            return;
        }

        sessionId = data.has("session_id") ? data.get("session_id").getAsString() : null;
        retryCount = 0;
        log.info("Connected to Kook gateway (sessionId={})", sessionId);
        startHeartbeat();
    }

    private void handleEvent(JsonObject obj) {
        var sn = obj.has("sn") ? obj.get("sn").getAsInt() : 0;
        if (sn > 0) {
            lastSn = sn;
        }

        var data = obj.getAsJsonObject("d");
        bot.dispatchEvent(data);
    }

    private void handlePong() {
        if (pongTimeoutFuture != null) {
            pongTimeoutFuture.cancel(false);
            pongTimeoutFuture = null;
        }
    }

    private void handleReconnect(JsonObject obj) {
        // Extract the error code if present (40106=missing params, 40107=session expired, 40108=invalid sn).
        int code = 0;
        if (obj.has("d") && obj.getAsJsonObject("d").has("code")) {
            code = obj.getAsJsonObject("d").get("code").getAsInt();
        }
        if (code == 40107 || code == 40108) {
            log.warn("Server requested reconnect: session invalid", new KookSessionExpiredException(code,
                    code == 40107 ? "Session expired" : "Invalid sn"));
        } else {
            log.info("Server requested reconnect (code={})", code);
        }
        sessionId = null;
        lastSn = 0;
        if (webSocket != null) {
            webSocket.close(1000, "Server reconnect");
        }
        stopHeartbeat();
        doConnect(false);
    }

    private void startHeartbeat() {
        stopHeartbeat();
        heartbeatFuture = scheduler.scheduleAtFixedRate(this::sendPing,
                HEARTBEAT_INTERVAL_MS, HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    private void stopHeartbeat() {
        if (heartbeatFuture != null) {
            heartbeatFuture.cancel(false);
            heartbeatFuture = null;
        }
        if (pongTimeoutFuture != null) {
            pongTimeoutFuture.cancel(false);
            pongTimeoutFuture = null;
        }
    }

    private void sendPing() {
        if (webSocket == null) return;

        var ping = new JsonObject();
        ping.addProperty("s", Signal.PING.value());
        ping.addProperty("sn", lastSn);
        webSocket.send(ping.toString());

        pongTimeoutFuture = scheduler.schedule(() -> {
            log.warn("Pong timeout, reconnecting...");
            if (webSocket != null) {
                webSocket.close(1000, "Pong timeout");
            }
            stopHeartbeat();
            doConnect(true);
        }, HELLO_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }

    private void scheduleReconnect() {
        if (!active) return;
        var delay = Math.min((long) Math.pow(2, retryCount) * 1000, MAX_RETRY_DELAY_MS);
        retryCount++;
        log.info("Reconnecting in {}ms (attempt {})", delay, retryCount);
        scheduler.schedule(() -> doConnect(sessionId != null), delay, TimeUnit.MILLISECONDS);
    }

    private String decompress(byte[] data) throws IOException {
        var inflater = new Inflater();
        inflater.setInput(data);
        var out = new ByteArrayOutputStream(data.length * 2);
        var buffer = new byte[4096];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                if (count == 0 && inflater.needsInput()) break;
                out.write(buffer, 0, count);
            }
        } catch (java.util.zip.DataFormatException e) {
            throw new IOException("Zlib decompression failed", e);
        } finally {
            inflater.end();
        }
        return out.toString(StandardCharsets.UTF_8);
    }
}
