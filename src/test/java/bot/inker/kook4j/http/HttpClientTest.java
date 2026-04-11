package bot.inker.kook4j.http;

import bot.inker.kook4j.exception.*;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class HttpClientTest {

    private static final String SUCCESS_JSON = """
            {"code":0,"message":"操作成功","data":{"id":"123","name":"test-guild"}}
            """;

    private static final String NOT_FOUND_JSON = """
            {"code":40012,"message":"该数据不存在或者你没有权限操作","data":{}}
            """;

    private static final String UNAUTHORIZED_JSON = """
            {"code":40101,"message":"无效的token","data":{}}
            """;

    private static final String FORBIDDEN_JSON = """
            {"code":40300,"message":"权限不足","data":{}}
            """;

    private static final String RATE_LIMIT_JSON = """
            {"code":40007,"message":"请求过于频繁","data":{}}
            """;

    private HttpClient clientWith(int httpCode, String json) {
        return clientWith(httpCode, json, Map.of());
    }

    private HttpClient clientWith(int httpCode, String json, Map<String, String> headers) {
        var okClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    var builder = new Response.Builder()
                            .request(chain.request())
                            .protocol(Protocol.HTTP_1_1)
                            .code(httpCode)
                            .message(httpCode == 200 ? "OK" : "Error")
                            .body(ResponseBody.create(json,
                                    MediaType.get("application/json; charset=utf-8")));
                    headers.forEach(builder::header);
                    return builder.build();
                })
                .build();
        return new HttpClient(okClient, "test-token");
    }

    private HttpClient clientWithInterceptor(Interceptor interceptor) {
        var okClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        return new HttpClient(okClient, "test-token");
    }

    @Test
    void get_returnsData_onSuccess() {
        var data = clientWith(200, SUCCESS_JSON).get("/guild/view", Map.of("guild_id", "123"));
        assertNotNull(data);
        var obj = data.getAsJsonObject();
        assertEquals("123", obj.get("id").getAsString());
        assertEquals("test-guild", obj.get("name").getAsString());
    }

    @Test
    void get_noParams_succeeds() {
        var data = clientWith(200, SUCCESS_JSON).get("/user/me");
        assertNotNull(data);
    }

    @Test
    void post_returnsData_onSuccess() {
        var body = new JsonObject();
        body.addProperty("name", "test");
        var data = clientWith(200, SUCCESS_JSON).post("/guild/create", body);
        assertNotNull(data);
        assertEquals("123", data.getAsJsonObject().get("id").getAsString());
    }

    @Test
    void get_throwsKookNotFoundException_on40012() {
        var ex = assertThrows(KookNotFoundException.class,
                () -> clientWith(200, NOT_FOUND_JSON).get("/guild/view"));
        assertEquals(40012, ex.code());
    }

    @Test
    void get_throwsKookUnauthorizedException_on401() {
        assertThrows(KookUnauthorizedException.class,
                () -> clientWith(401, UNAUTHORIZED_JSON).get("/user/me"));
    }

    @Test
    void get_throwsKookForbiddenException_on403() {
        assertThrows(KookForbiddenException.class,
                () -> clientWith(403, FORBIDDEN_JSON).get("/guild/view"));
    }

    @Test
    void get_throwsKookApiException_onUnknownApiCode() {
        var json = "{\"code\":99999,\"message\":\"unknown error\",\"data\":{}}";
        var ex = assertThrows(KookApiException.class,
                () -> clientWith(200, json).get("/guild/view"));
        assertEquals(99999, ex.code());
    }

    @Test
    void get_throwsException_onUnparsableResponseBody() {
        // Non-JSON body should cause a parse failure
        var client = clientWith(200, "this is not json at all!!!");
        assertThrows(Exception.class, () -> client.get("/guild/view"));
    }

    @Test
    void get_throwsKookApiException_on500WithEmptyBody() {
        var client = clientWith(500,
                "{\"code\":-1,\"message\":\"Internal Server Error\",\"data\":{}}");
        assertThrows(KookApiException.class, () -> client.get("/guild/view"));
    }

    @Test
    void get_retriesOnce_andSucceeds_after429() throws Exception {
        var callCount = new AtomicInteger(0);
        var client = clientWithInterceptor(chain -> {
            int n = callCount.incrementAndGet();
            if (n == 1) {
                return new Response.Builder()
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .code(429).message("Rate Limited")
                        .header("X-Rate-Limit-Bucket", "test")
                        .header("X-Rate-Limit-Limit", "5")
                        .header("X-Rate-Limit-Remaining", "0")
                        .header("X-Rate-Limit-Reset", "0")   // retry immediately
                        .body(ResponseBody.create(RATE_LIMIT_JSON,
                                MediaType.get("application/json")))
                        .build();
            }
            return new Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(200).message("OK")
                    .body(ResponseBody.create(SUCCESS_JSON,
                            MediaType.get("application/json; charset=utf-8")))
                    .build();
        });

        var data = client.get("/guild/view");
        assertNotNull(data);
        assertEquals(2, callCount.get(), "Expected exactly 1 retry (2 total calls)");
    }

    @Test
    void get_throwsKookRateLimitException_afterMaxRetries() {
        var client = clientWithInterceptor(chain -> new Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(429).message("Rate Limited")
                .header("X-Rate-Limit-Bucket", "test")
                .header("X-Rate-Limit-Limit", "5")
                .header("X-Rate-Limit-Remaining", "0")
                .header("X-Rate-Limit-Reset", "0")
                .body(ResponseBody.create(RATE_LIMIT_JSON,
                        MediaType.get("application/json")))
                .build());

        assertThrows(KookRateLimitException.class, () -> client.get("/guild/view"));
    }

    @Test
    void get_retriesUpToMaxRetries_thenThrows() {
        var callCount = new AtomicInteger(0);
        var client = clientWithInterceptor(chain -> {
            callCount.incrementAndGet();
            return new Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(429).message("Rate Limited")
                    .header("X-Rate-Limit-Bucket", "test")
                    .header("X-Rate-Limit-Limit", "5")
                    .header("X-Rate-Limit-Remaining", "0")
                    .header("X-Rate-Limit-Reset", "0")
                    .body(ResponseBody.create(RATE_LIMIT_JSON,
                            MediaType.get("application/json")))
                    .build();
        });

        assertThrows(KookRateLimitException.class, () -> client.get("/guild/view"));
        // MAX_RETRIES = 5, so 6 total attempts (initial + 5 retries)
        assertEquals(6, callCount.get(), "Should attempt exactly MAX_RETRIES+1 times");
    }

    @Test
    void getAsync_returnsCompletedFuture_onSuccess() throws Exception {
        var future = clientWith(200, SUCCESS_JSON).getAsync("/user/me");
        assertNotNull(future);
        var data = future.get(5, TimeUnit.SECONDS);
        assertNotNull(data);
        assertEquals("123", data.getAsJsonObject().get("id").getAsString());
    }

    @Test
    void postAsync_returnsCompletedFuture_onSuccess() throws Exception {
        var body = new JsonObject();
        var data = clientWith(200, SUCCESS_JSON)
                .postAsync("/guild/create", body)
                .get(5, TimeUnit.SECONDS);
        assertNotNull(data);
    }

    @Test
    void getAsync_propagatesException_asCompletionException() {
        var future = clientWith(200, NOT_FOUND_JSON).getAsync("/guild/view");
        var ex = assertThrows(CompletionException.class, future::join);
        assertInstanceOf(KookNotFoundException.class, ex.getCause());
        assertEquals(40012, ((KookNotFoundException) ex.getCause()).code());
    }

    @Test
    void postAsync_propagatesException_asCompletionException() {
        var body = new JsonObject();
        var future = clientWith(401, UNAUTHORIZED_JSON).postAsync("/user/me", body);
        var ex = assertThrows(CompletionException.class, future::join);
        assertInstanceOf(KookUnauthorizedException.class, ex.getCause());
    }

    @Test
    void syncAndAsync_returnIdenticalResult() throws Exception {
        var client = clientWith(200, SUCCESS_JSON);
        var sync  = client.get("/user/me");
        var async = client.getAsync("/user/me").get(5, TimeUnit.SECONDS);
        assertEquals(sync, async);
    }

    @Test
    void multipleAsyncRequests_allComplete_concurrently() throws Exception {
        var client = clientWith(200, SUCCESS_JSON);
        int n = 20;
        @SuppressWarnings("unchecked")
        var futures = new CompletableFuture[n];
        for (int i = 0; i < n; i++) {
            futures[i] = client.getAsync("/user/me");
        }
        CompletableFuture.allOf(futures).get(10, TimeUnit.SECONDS);
        for (var f : futures) {
            assertNotNull(f.join());
        }
    }

    @Test
    void mixedSyncAndAsync_bothSucceed() throws Exception {
        var client = clientWith(200, SUCCESS_JSON);

        // Fire async first, then call sync, then check async result
        var future = client.getAsync("/user/me");
        var syncResult = client.get("/user/me");
        var asyncResult = future.get(5, TimeUnit.SECONDS);

        assertNotNull(syncResult);
        assertNotNull(asyncResult);
        assertEquals(syncResult, asyncResult);
    }

    @Test
    void request_includesAuthorizationHeader() {
        var capturedHeaders = new java.util.concurrent.atomic.AtomicReference<Headers>();
        var okClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    capturedHeaders.set(chain.request().headers());
                    return new Response.Builder()
                            .request(chain.request())
                            .protocol(Protocol.HTTP_1_1)
                            .code(200).message("OK")
                            .body(ResponseBody.create(SUCCESS_JSON,
                                    MediaType.get("application/json; charset=utf-8")))
                            .build();
                })
                .build();
        new HttpClient(okClient, "my-secret-token").get("/user/me");

        assertNotNull(capturedHeaders.get());
        assertEquals("Bot my-secret-token",
                capturedHeaders.get().get("Authorization"));
    }
}
