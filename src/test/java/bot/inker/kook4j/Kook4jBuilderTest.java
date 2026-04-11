package bot.inker.kook4j;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class Kook4jBuilderTest {

    @Test
    void build_throwsNPE_whenTokenMissing() {
        assertThrows(NullPointerException.class, () -> Kook4j.builder().build());
    }

    @Test
    void build_succeeds_withMinimalConfig() {
        var bot = Kook4j.builder().token("test-token").build();
        assertNotNull(bot);
        bot.stop();
    }

    @Test
    void build_usesProvidedOkHttpClient() {
        var custom = new OkHttpClient.Builder()
                .connectTimeout(7, TimeUnit.SECONDS)
                .build();
        var bot = Kook4j.builder()
                .token("test-token")
                .okHttpClient(custom)
                .build();
        assertSame(custom, bot.httpClient().okHttpClient(),
                "Provided OkHttpClient should be used as-is");
        bot.stop();
    }

    @Test
    void build_acceptsCustomConnectTimeout() {
        assertDoesNotThrow(() -> {
            var bot = Kook4j.builder()
                    .token("test-token")
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();
            bot.stop();
        });
    }

    @Test
    void build_acceptsCustomReadTimeout() {
        assertDoesNotThrow(() -> {
            var bot = Kook4j.builder()
                    .token("test-token")
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();
            bot.stop();
        });
    }

    @Test
    void build_acceptsCustomWriteTimeout() {
        assertDoesNotThrow(() -> {
            var bot = Kook4j.builder()
                    .token("test-token")
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            bot.stop();
        });
    }

    @Test
    void build_acceptsAllTimeoutsChained() {
        assertDoesNotThrow(() -> {
            var bot = Kook4j.builder()
                    .token("test-token")
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            bot.stop();
        });
    }

    @Test
    void build_withCompressFalse_succeeds() {
        assertDoesNotThrow(() -> {
            var bot = Kook4j.builder()
                    .token("test-token")
                    .compress(false)
                    .build();
            bot.stop();
        });
    }

    @Test
    void build_builderIsReusable() {
        // Building twice from the same builder should produce independent instances
        var builder = Kook4j.builder().token("test-token");
        var bot1 = builder.build();
        var bot2 = builder.build();
        assertNotSame(bot1, bot2);
        bot1.stop();
        bot2.stop();
    }

    @Test
    void build_timeoutsInMilliseconds() {
        // Verify that milliseconds conversion works (3500ms = 3.5s)
        assertDoesNotThrow(() -> {
            var bot = Kook4j.builder()
                    .token("test-token")
                    .connectTimeout(3500, TimeUnit.MILLISECONDS)
                    .readTimeout(45000, TimeUnit.MILLISECONDS)
                    .build();
            bot.stop();
        });
    }
}
