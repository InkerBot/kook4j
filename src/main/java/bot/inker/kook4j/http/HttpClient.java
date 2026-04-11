package bot.inker.kook4j.http;

import bot.inker.kook4j.Kook4jCodec;
import bot.inker.kook4j.exception.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public final class HttpClient {

    private static final Logger log = LoggerFactory.getLogger(HttpClient.class);
    private static final String BASE_URL = "https://www.kookapp.cn/api/v3";
    private static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    private static final int MAX_RETRIES = 5;

    private final OkHttpClient client;
    private final String token;
    private final RateLimiter rateLimiter = new RateLimiter();

    public HttpClient(OkHttpClient client, String token) {
        this.client = client;
        this.token = token;
    }

    private static <T> T joinUnwrapped(CompletableFuture<T> future) {
        try {
            return future.join();
        } catch (CompletionException e) {
            var cause = e.getCause();
            if (cause instanceof RuntimeException re) throw re;
            throw new KookConnectionException("Unexpected error", cause);
        }
    }

    private static KookApiException mapApiCode(int httpCode, int apiCode, String message) {
        if (httpCode == 401) return new KookUnauthorizedException(apiCode, message);
        if (httpCode == 403) return new KookForbiddenException(apiCode, message);

        return switch (apiCode / 100) {
            case 401 -> new KookUnauthorizedException(apiCode, message);
            case 403 -> new KookForbiddenException(apiCode, message);
            default -> mapBySpecificCode(apiCode, message);
        };
    }

    private static KookApiException mapBySpecificCode(int apiCode, String message) {
        return switch (apiCode) {
            // Not found / no permission to access resource
            case 40012 -> new KookNotFoundException(apiCode, message);
            // Bad request range (HTTP API)
            case 40000, 40001, 40002, 40003, 40004, 40005, 40006, 40007, 40008, 40009, 40010, 40011 ->
                    new KookBadRequestException(apiCode, message);
            // WebSocket HELLO errors
            case 40100, 40106 -> new KookBadRequestException(apiCode, message);      // missing params
            case 40101, 40102 -> new KookUnauthorizedException(apiCode, message);    // invalid / failed token
            case 40103 -> new KookTokenExpiredException(apiCode, message);           // token expired
            // WebSocket RECONNECT / resume errors
            case 40107, 40108 -> new KookSessionExpiredException(apiCode, message);  // session / sn expired
            default -> new KookApiException(apiCode, message);
        };
    }

    private static KookApiException mapHttpStatus(int httpCode, int apiCode, String message) {
        return switch (httpCode) {
            case 401 -> new KookUnauthorizedException(apiCode, message);
            case 403 -> new KookForbiddenException(apiCode, message);
            case 404 -> new KookNotFoundException(apiCode, message);
            default -> new KookApiException(apiCode, message);
        };
    }

    private static KookRateLimitException buildRateLimitException(Response response) {
        var limit = parseIntHeader(response, "X-Rate-Limit-Limit", 0);
        var remaining = parseIntHeader(response, "X-Rate-Limit-Remaining", 0);
        var reset = parseIntHeader(response, "X-Rate-Limit-Reset", 0);
        var bucket = response.header("X-Rate-Limit-Bucket", "");
        var global = response.header("X-Rate-Limit-Global") != null;

        String message;
        try {
            var body = response.body();
            if (body != null) {
                var apiResponse = Kook4jCodec.fromJson(body.string(), ApiResponse.class);
                message = apiResponse.message();
            } else {
                message = "Rate limit exceeded";
            }
        } catch (Exception e) {
            message = "Rate limit exceeded";
        }

        return new KookRateLimitException(429, message, limit, remaining, reset, bucket, global);
    }

    private static int parseIntHeader(Response response, String name, int defaultValue) {
        var value = response.header(name);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public JsonElement get(String path) {
        return joinUnwrapped(getAsync(path));
    }

    public JsonElement get(String path, Map<String, String> params) {
        return joinUnwrapped(getAsync(path, params));
    }

    public JsonElement post(String path, JsonObject body) {
        return joinUnwrapped(postAsync(path, body));
    }

    public JsonElement postForm(String path, Map<String, String> fields, File file) {
        return joinUnwrapped(postFormAsync(path, fields, file));
    }

    public JsonElement postForm(String path, Map<String, String> fields, String fileFieldName, File file) {
        return joinUnwrapped(postFormAsync(path, fields, fileFieldName, file));
    }

    public JsonElement postWithQuery(String path, Map<String, String> queryParams, JsonObject body) {
        return joinUnwrapped(postWithQueryAsync(path, queryParams, body));
    }

    public byte[] getRawBytes(String path, Map<String, String> params) {
        return joinUnwrapped(getRawBytesAsync(path, params));
    }

    public String getGatewayUrl(boolean compress) {
        return joinUnwrapped(getGatewayUrlAsync(compress));
    }

    public CompletableFuture<JsonElement> getAsync(String path) {
        return getAsync(path, Map.of());
    }

    public CompletableFuture<JsonElement> getAsync(String path, Map<String, String> params) {
        var urlBuilder = HttpUrl.parse(BASE_URL + path).newBuilder();
        params.forEach(urlBuilder::addQueryParameter);
        var request = new Request.Builder()
                .url(urlBuilder.build())
                .header("Authorization", "Bot " + token)
                .get()
                .build();
        return executeAsync(request, 0);
    }

    public CompletableFuture<JsonElement> postAsync(String path, JsonObject body) {
        var request = new Request.Builder()
                .url(BASE_URL + path)
                .header("Authorization", "Bot " + token)
                .post(RequestBody.create(body.toString(), JSON_TYPE))
                .build();
        return executeAsync(request, 0);
    }

    public CompletableFuture<JsonElement> postFormAsync(String path, Map<String, String> fields, File file) {
        return postFormAsync(path, fields, "file", file);
    }

    public CompletableFuture<JsonElement> postFormAsync(String path, Map<String, String> fields, String fileFieldName, File file) {
        var builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        fields.forEach(builder::addFormDataPart);
        if (file != null) {
            builder.addFormDataPart(fileFieldName, file.getName(),
                    RequestBody.create(file, MediaType.parse("application/octet-stream")));
        }
        var request = new Request.Builder()
                .url(BASE_URL + path)
                .header("Authorization", "Bot " + token)
                .post(builder.build())
                .build();
        return executeAsync(request, 0);
    }

    public CompletableFuture<JsonElement> postWithQueryAsync(String path, Map<String, String> queryParams, JsonObject body) {
        var urlBuilder = HttpUrl.parse(BASE_URL + path).newBuilder();
        queryParams.forEach(urlBuilder::addQueryParameter);
        var request = new Request.Builder()
                .url(urlBuilder.build())
                .header("Authorization", "Bot " + token)
                .post(RequestBody.create(body.toString(), JSON_TYPE))
                .build();
        return executeAsync(request, 0);
    }

    public CompletableFuture<byte[]> getRawBytesAsync(String path, Map<String, String> params) {
        var urlBuilder = HttpUrl.parse(BASE_URL + path).newBuilder();
        params.forEach(urlBuilder::addQueryParameter);
        var request = new Request.Builder()
                .url(urlBuilder.build())
                .header("Authorization", "Bot " + token)
                .get()
                .build();
        var future = new CompletableFuture<byte[]>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(
                        new KookConnectionException("HTTP request failed: " + e.getMessage(), e));
            }

            @Override
            public void onResponse(Call call, Response response) {
                try (response) {
                    var responseBody = response.body();
                    if (responseBody == null) {
                        future.completeExceptionally(
                                new KookConnectionException("Empty response body"));
                        return;
                    }
                    try {
                        future.complete(responseBody.bytes());
                    } catch (IOException e) {
                        future.completeExceptionally(
                                new KookConnectionException("Failed to read response body", e));
                    }
                }
            }
        });
        return future;
    }

    public CompletableFuture<String> getGatewayUrlAsync(boolean compress) {
        return getAsync("/gateway/index", Map.of("compress", compress ? "1" : "0"))
                .thenApply(data -> data.getAsJsonObject().get("url").getAsString());
    }

    public OkHttpClient okHttpClient() {
        return client;
    }

    private CompletableFuture<JsonElement> executeAsync(Request request, int attempt) {
        var bucket = RateLimiter.bucketFromPath(request.url().encodedPath());

        return rateLimiter.acquirePermitAsync(bucket).thenCompose(__ -> {
            var future = new CompletableFuture<JsonElement>();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    future.completeExceptionally(
                            new KookConnectionException("HTTP request failed: " + e.getMessage(), e));
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try (response) {
                        rateLimiter.update(response);
                        var httpCode = response.code();

                        if (httpCode == 429) {
                            if (attempt >= MAX_RETRIES) {
                                future.completeExceptionally(buildRateLimitException(response));
                                return;
                            }
                            var retryMs = rateLimiter.getRetryAfterMs(response);
                            log.warn("Rate limited on {} (attempt {}/{}), retrying in {}ms",
                                    bucket, attempt + 1, MAX_RETRIES, retryMs);
                            // Non-blocking delay, then recurse with incremented attempt
                            rateLimiter.delayAsync(retryMs)
                                    .thenCompose(___ -> executeAsync(request, attempt + 1))
                                    .whenComplete((result, ex) -> {
                                        if (ex != null) future.completeExceptionally(ex);
                                        else future.complete(result);
                                    });
                            return;
                        }

                        var responseBody = response.body();
                        if (responseBody == null) {
                            future.completeExceptionally(mapHttpStatus(httpCode, -1, "Empty response body"));
                            return;
                        }

                        String json;
                        try {
                            json = responseBody.string();
                        } catch (IOException e) {
                            future.completeExceptionally(
                                    new KookConnectionException("Failed to read response body", e));
                            return;
                        }

                        var apiResponse = Kook4jCodec.fromJson(json, ApiResponse.class);
                        if (!apiResponse.success()) {
                            future.completeExceptionally(
                                    mapApiCode(httpCode, apiResponse.code(), apiResponse.message()));
                            return;
                        }

                        future.complete(apiResponse.data());
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                    }
                }
            });

            return future;
        });
    }
}
