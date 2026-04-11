package bot.inker.kook4j.oauth;

import bot.inker.kook4j.Kook4jCodec;
import bot.inker.kook4j.entity.Guild;
import bot.inker.kook4j.entity.PagedList;
import bot.inker.kook4j.entity.User;
import bot.inker.kook4j.exception.KookApiException;
import bot.inker.kook4j.exception.KookConnectionException;
import bot.inker.kook4j.exception.KookUnauthorizedException;
import bot.inker.kook4j.http.ApiResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class KookOAuth {

    private static final String BASE_URL = "https://www.kookapp.cn";
    private static final String AUTHORIZE_URL = BASE_URL + "/app/oauth2/authorize";
    private static final String TOKEN_URL = BASE_URL + "/api/oauth2/token";
    private static final String API_BASE = BASE_URL + "/api/v3";
    private static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

    private KookOAuth(Builder builder) {
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.redirectUri = builder.redirectUri;
        this.client = builder.okHttpClient != null ? builder.okHttpClient : new OkHttpClient.Builder()
                .connectTimeout(builder.connectTimeoutMs, TimeUnit.MILLISECONDS)
                .readTimeout(builder.readTimeoutMs, TimeUnit.MILLISECONDS)
                .writeTimeout(builder.writeTimeoutMs, TimeUnit.MILLISECONDS)
                .build();
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static <T> T sync(CompletableFuture<T> future) {
        try {
            return future.join();
        } catch (CompletionException e) {
            var cause = e.getCause();
            if (cause instanceof RuntimeException re) throw re;
            throw new KookConnectionException("Unexpected error", cause);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public String authorizeUrl(OAuthScope... scopes) {
        return authorizeUrl(null, scopes);
    }

    public String authorizeUrl(String state, OAuthScope... scopes) {
        var scopeStr = Arrays.stream(scopes)
                .map(OAuthScope::value)
                .collect(Collectors.joining(" "));

        var sb = new StringBuilder(AUTHORIZE_URL)
                .append("?client_id=").append(encode(clientId))
                .append("&redirect_uri=").append(encode(redirectUri))
                .append("&response_type=code")
                .append("&scope=").append(encode(scopeStr));

        if (state != null) {
            sb.append("&state=").append(encode(state));
        }

        return sb.toString();
    }

    public CompletableFuture<OAuthToken> getAccessTokenAsync(String code) {
        var body = new JsonObject();
        body.addProperty("grant_type", "authorization_code");
        body.addProperty("client_id", clientId);
        body.addProperty("client_secret", clientSecret);
        body.addProperty("code", code);
        body.addProperty("redirect_uri", redirectUri);

        var request = new Request.Builder()
                .url(TOKEN_URL)
                .post(RequestBody.create(body.toString(), JSON_TYPE))
                .build();

        var future = new CompletableFuture<OAuthToken>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(
                        new KookConnectionException("OAuth token request failed: " + e.getMessage(), e));
            }

            @Override
            public void onResponse(Call call, Response response) {
                try (response) {
                    if (response.code() == 401) {
                        future.completeExceptionally(
                                new KookUnauthorizedException(401, "Invalid OAuth credentials"));
                        return;
                    }

                    var responseBody = response.body();
                    if (responseBody == null) {
                        future.completeExceptionally(
                                new KookApiException(-1, "Empty response body"));
                        return;
                    }

                    var json = responseBody.string();
                    future.complete(Kook4jCodec.fromJson(json, OAuthToken.class));
                } catch (IOException e) {
                    future.completeExceptionally(
                            new KookConnectionException("OAuth token request failed: " + e.getMessage(), e));
                }
            }
        });
        return future;
    }

    public CompletableFuture<User> userMeAsync(String accessToken) {
        return oauthGetAsync("/user/me", accessToken)
                .thenApply(data -> Kook4jCodec.fromJson(data, User.class));
    }

    public CompletableFuture<PagedList<Guild>> userGuildsAsync(String accessToken) {
        return userGuildsAsync(accessToken, 1, 50);
    }

    public CompletableFuture<PagedList<Guild>> userGuildsAsync(String accessToken, int page, int pageSize) {
        return oauthGetAsync("/guild/list?page=" + page + "&page_size=" + pageSize, accessToken)
                .thenApply(data -> {
                    var obj = data.getAsJsonObject();
                    List<Guild> items = Kook4jCodec.fromJson(obj.get("items"), new TypeToken<List<Guild>>() {
                    }.getType());
                    var meta = Kook4jCodec.fromJson(obj.get("meta"), PagedList.Meta.class);
                    return new PagedList<>(items, meta, (p, ps) -> userGuildsAsync(accessToken, p, ps));
                });
    }

    public OAuthToken getAccessToken(String code) {
        return sync(getAccessTokenAsync(code));
    }

    public User userMe(String accessToken) {
        return sync(userMeAsync(accessToken));
    }

    public PagedList<Guild> userGuilds(String accessToken) {
        return sync(userGuildsAsync(accessToken));
    }

    public PagedList<Guild> userGuilds(String accessToken, int page, int pageSize) {
        return sync(userGuildsAsync(accessToken, page, pageSize));
    }

    private CompletableFuture<JsonElement> oauthGetAsync(String path, String accessToken) {
        var request = new Request.Builder()
                .url(API_BASE + path)
                .header("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        var future = new CompletableFuture<JsonElement>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(
                        new KookConnectionException("OAuth API request failed: " + e.getMessage(), e));
            }

            @Override
            public void onResponse(Call call, Response response) {
                try (response) {
                    if (response.code() == 401) {
                        future.completeExceptionally(
                                new KookUnauthorizedException(401, "Invalid or expired access token"));
                        return;
                    }

                    var responseBody = response.body();
                    if (responseBody == null) {
                        future.completeExceptionally(
                                new KookApiException(-1, "Empty response body"));
                        return;
                    }

                    var json = responseBody.string();
                    var apiResponse = Kook4jCodec.fromJson(json, ApiResponse.class);

                    if (!apiResponse.success()) {
                        future.completeExceptionally(
                                new KookApiException(apiResponse.code(), apiResponse.message()));
                        return;
                    }

                    future.complete(apiResponse.data());
                } catch (IOException e) {
                    future.completeExceptionally(
                            new KookConnectionException("OAuth API request failed: " + e.getMessage(), e));
                }
            }
        });
        return future;
    }

    public static final class Builder {

        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private OkHttpClient okHttpClient;

        // timeout defaults (in ms)
        private long connectTimeoutMs = 10_000;
        private long readTimeoutMs = 30_000;
        private long writeTimeoutMs = 30_000;

        Builder() {
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder redirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public Builder okHttpClient(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            return this;
        }

        public Builder connectTimeout(long timeout, TimeUnit unit) {
            this.connectTimeoutMs = unit.toMillis(timeout);
            return this;
        }

        public Builder readTimeout(long timeout, TimeUnit unit) {
            this.readTimeoutMs = unit.toMillis(timeout);
            return this;
        }

        public Builder writeTimeout(long timeout, TimeUnit unit) {
            this.writeTimeoutMs = unit.toMillis(timeout);
            return this;
        }

        public KookOAuth build() {
            Objects.requireNonNull(clientId, "clientId must not be null");
            Objects.requireNonNull(clientSecret, "clientSecret must not be null");
            Objects.requireNonNull(redirectUri, "redirectUri must not be null");
            return new KookOAuth(this);
        }
    }
}
