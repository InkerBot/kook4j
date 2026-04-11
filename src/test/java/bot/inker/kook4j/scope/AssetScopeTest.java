package bot.inker.kook4j.scope;

import bot.inker.kook4j.BotInstance;
import bot.inker.kook4j.Kook4j;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AssetScopeTest {

    private static final String TOKEN = System.getProperty("KOOK_TOKEN");

    private static BotInstance bot;

    @BeforeAll
    static void setup() {
        assumeTrue(TOKEN != null && !TOKEN.isBlank(), "KOOK_TOKEN not set, skipping integration tests");
        bot = Kook4j.builder().token(TOKEN).build();
    }

    @AfterAll
    static void teardown() {
        if (bot != null) bot.stop();
    }

    @Test
    @Order(1)
    void testUploadAsset() throws Exception {
        var tmpFile = File.createTempFile("kook4j-test", ".txt");
        tmpFile.deleteOnExit();
        Files.writeString(tmpFile.toPath(), "kook4j integration test asset");

        String url = bot.assets().upload(tmpFile);
        assertNotNull(url);
        assertFalse(url.isBlank());
        assertTrue(url.startsWith("https://"), "Asset URL should start with https://");
        System.out.println("Uploaded asset URL: " + url);
    }

    @Test
    @Order(2)
    void testUploadAssetAsync() throws Exception {
        var tmpFile = File.createTempFile("kook4j-test-async", ".txt");
        tmpFile.deleteOnExit();
        Files.writeString(tmpFile.toPath(), "kook4j async integration test asset");

        String url = bot.assets().uploadAsync(tmpFile).get(15, TimeUnit.SECONDS);
        assertNotNull(url);
        assertFalse(url.isBlank());
        assertTrue(url.startsWith("https://"), "Async asset URL should start with https://");
        System.out.println("Async uploaded asset URL: " + url);
    }
}