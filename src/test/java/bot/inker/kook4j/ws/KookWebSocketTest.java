package bot.inker.kook4j.ws;

import bot.inker.kook4j.http.HttpClient;
import okhttp3.*;
import okio.ByteString;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class KookWebSocketTest {

    private static final String GATEWAY_JSON = """
            {"code":0,"message":"ok","data":{"url":"wss://gateway.example/socket?compress=0"}}
            """;

    @Test
    void helloFailure_stopsReconnectLoop() throws Exception {
        var fixture = new GatewayFixture();
        var socket = fixture.createSocket(100, 20);

        socket.connect();
        assertEquals(1, fixture.connectCount.get());

        var first = fixture.latestSocket();
        socket.onMessage(first, hello(40103, null));

        assertEquals(1, fixture.connectCount.get(), "HELLO auth failures must not reconnect");
        assertEquals(1, first.closeCount.get(), "Failing socket should be closed exactly once");
        assertFalse((boolean) getField(socket, "active"));
    }

    @Test
    void reconnectSignal_reconnectsOnlyOnce() {
        var fixture = new GatewayFixture();
        var socket = fixture.createSocket(100, 20);

        socket.connect();
        var first = fixture.latestSocket();
        socket.onMessage(first, hello(0, "session-1"));
        socket.onMessage(first, reconnect(0));

        assertEquals(2, fixture.connectCount.get(), "Server reconnect should create exactly one new socket");
        assertEquals(1, first.closeCount.get(), "Original socket should be closed exactly once");
        assertNotSame(first, fixture.latestSocket());
    }

    @Test
    void pongTimeout_reconnectsOnlyOnce() throws Exception {
        var fixture = new GatewayFixture();
        var socket = fixture.createSocket(1_000, 25);

        socket.connect();
        var first = fixture.latestSocket();
        socket.onMessage(first, hello(0, "session-1"));

        invokeNoArg(socket, "sendPing");
        waitUntil(() -> fixture.connectCount.get() == 2, 1_000);
        Thread.sleep(50);

        assertEquals(2, fixture.connectCount.get(), "Pong timeout should only reconnect once");
        assertEquals(1, first.closeCount.get(), "Timed out socket should only be closed once");
    }

    @Test
    void disconnect_allowsLaterRestart() throws Exception {
        var fixture = new GatewayFixture();
        var socket = fixture.createSocket(100, 20);

        socket.connect();
        var first = fixture.latestSocket();
        socket.onMessage(first, hello(0, "session-1"));
        var firstScheduler = (ScheduledExecutorService) getField(socket, "scheduler");
        assertNotNull(getField(socket, "heartbeatFuture"));

        socket.disconnect();

        socket.connect();
        var second = fixture.latestSocket();
        socket.onMessage(second, hello(0, "session-2"));

        var secondScheduler = (ScheduledExecutorService) getField(socket, "scheduler");
        assertEquals(2, fixture.connectCount.get(), "Restart should establish a new connection");
        assertNotSame(firstScheduler, secondScheduler, "Scheduler should be recreated after stop()");
        assertNotNull(getField(socket, "heartbeatFuture"), "Heartbeat should be scheduled after restart");

        socket.disconnect();
    }

    private static String hello(int code, String sessionId) {
        if (sessionId == null) {
            return """
                    {"s":1,"d":{"code":%d}}
                    """.formatted(code);
        }
        return """
                {"s":1,"d":{"code":%d,"session_id":"%s"}}
                """.formatted(code, sessionId);
    }

    private static String reconnect(int code) {
        return """
                {"s":5,"d":{"code":%d}}
                """.formatted(code);
    }

    private static Object getField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    private static void invokeNoArg(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }

    private static void waitUntil(CheckedCondition condition, long timeoutMs) throws Exception {
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMs);
        while (System.nanoTime() < deadline) {
            if (condition.getAsBoolean()) {
                return;
            }
            Thread.sleep(10);
        }
        fail("Condition was not met within " + timeoutMs + "ms");
    }

    @FunctionalInterface
    private interface CheckedCondition {
        boolean getAsBoolean() throws Exception;
    }

    private static final class GatewayFixture {
        private final AtomicInteger connectCount = new AtomicInteger();
        private final List<TestWebSocket> sockets = new CopyOnWriteArrayList<>();
        private final HttpClient httpClient = new HttpClient(new OkHttpClient.Builder()
                .addInterceptor(chain -> new Response.Builder()
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .code(200).message("OK")
                        .body(ResponseBody.create(GATEWAY_JSON, MediaType.get("application/json; charset=utf-8")))
                        .build())
                .build(), "test-token");

        private KookWebSocket createSocket(int heartbeatIntervalMs, int pongTimeoutMs) {
            return new KookWebSocket(data -> {
            }, httpClient, false, (url, listener) -> {
                connectCount.incrementAndGet();
                var socket = new TestWebSocket(url, listener);
                sockets.add(socket);
                return socket;
            }, heartbeatIntervalMs, pongTimeoutMs, 100);
        }

        private TestWebSocket latestSocket() {
            return sockets.get(sockets.size() - 1);
        }
    }

    private static final class TestWebSocket implements WebSocket {
        private final WebSocketListener listener;
        private final Request request;
        private final AtomicInteger closeCount = new AtomicInteger();
        private volatile boolean closed;

        private TestWebSocket(String url, WebSocketListener listener) {
            this.listener = listener;
            this.request = new Request.Builder().url(url).build();
        }

        @Override
        public Request request() {
            return request;
        }

        @Override
        public long queueSize() {
            return 0;
        }

        @Override
        public boolean send(String text) {
            return !closed;
        }

        @Override
        public boolean send(ByteString bytes) {
            return !closed;
        }

        @Override
        public boolean close(int code, String reason) {
            if (closed) {
                return false;
            }
            closed = true;
            closeCount.incrementAndGet();
            listener.onClosed(this, code, reason);
            return true;
        }

        @Override
        public void cancel() {
            closed = true;
        }
    }
}
