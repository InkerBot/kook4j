package bot.inker.kook4j;

import bot.inker.kook4j.http.HttpClient;
import okhttp3.OkHttpClient;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class Kook4j {

    private Kook4j() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String token;
        private boolean compress = true;
        private OkHttpClient okHttpClient;

        // timeout defaults (in ms)
        private long connectTimeoutMs = 10_000;
        private long readTimeoutMs = 30_000;
        private long writeTimeoutMs = 30_000;

        Builder() {
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder compress(boolean compress) {
            this.compress = compress;
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

        public BotInstance build() {
            Objects.requireNonNull(token, "token must not be null");

            var client = okHttpClient != null ? okHttpClient : new OkHttpClient.Builder()
                    .connectTimeout(connectTimeoutMs, TimeUnit.MILLISECONDS)
                    .readTimeout(readTimeoutMs, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeoutMs, TimeUnit.MILLISECONDS)
                    .build();

            var http = new HttpClient(client, token);
            return new BotInstance(http, compress);
        }
    }
}
