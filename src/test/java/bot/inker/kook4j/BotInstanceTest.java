package bot.inker.kook4j;

import bot.inker.kook4j.entity.*;
import bot.inker.kook4j.entity.OnlineStatus;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BotInstanceTest {

    private static final String TOKEN = System.getProperty("KOOK_TOKEN");

    private static BotInstance bot;

    @BeforeAll
    static void setup() {
        assumeTrue(TOKEN != null && !TOKEN.isBlank(), "KOOK_TOKEN not set, skipping integration tests");
        bot = Kook4j.builder()
                .token(TOKEN)
                .build();
    }

    @AfterAll
    static void teardown() {
        if (bot != null) {
            bot.stop();
        }
    }

    @Test
    @Order(1)
    void testGetCurrentUser() {
        User me = bot.me();
        assertNotNull(me);
        assertNotNull(me.id());
        assertNotNull(me.username());
        System.out.println("Bot user: " + me.username() + "#" + me.identifyNum() + " (id=" + me.id() + ")");
    }

    @Test
    @Order(2)
    void testGetCurrentUserAsync() throws Exception {
        var future = bot.meAsync();
        assertNotNull(future);
        User me = future.get(10, TimeUnit.SECONDS);
        assertNotNull(me);
        assertNotNull(me.id());
        assertNotNull(me.username());
        System.out.println("Async bot user: " + me.username());
    }

    @Test
    @Order(3)
    void testListGuilds() {
        PagedList<Guild> guilds = bot.guilds();
        assertNotNull(guilds);
        assertFalse(guilds.isEmpty(), "Bot should be in at least one guild");
        assertTrue(guilds.meta().total() > 0);
        System.out.println("Found " + guilds.meta().total() + " guilds");
    }

    @Test
    @Order(4)
    void testListGuildsAsync() throws Exception {
        PagedList<Guild> guilds = bot.guildsAsync().get(10, TimeUnit.SECONDS);
        assertNotNull(guilds);
        assertFalse(guilds.isEmpty());
        assertEquals(bot.guilds().getFirst().id(), guilds.getFirst().id());
        System.out.println("Async guilds count: " + guilds.meta().total());
    }

    @Test
    @Order(5)
    void testListUserChats() {
        PagedList<UserChat> chats = bot.userChats();
        assertNotNull(chats);
        assertNotNull(chats);
        System.out.println("Bot has " + chats.meta().total() + " DM sessions");
    }

    @Test
    @Order(6)
    void testListUserChatsAsync() throws Exception {
        PagedList<UserChat> chats = bot.userChatsAsync().get(10, TimeUnit.SECONDS);
        assertNotNull(chats);
        assertNotNull(chats);
        System.out.println("Async DM sessions: " + chats.meta().total());
    }

    @Test
    @Order(7)
    void testListVoiceChannels() {
        PagedList<VoiceChannel> channels = bot.voiceChannels();
        assertNotNull(channels);
        assertNotNull(channels);
        System.out.println("Bot in " + channels.meta().total() + " voice channels");
    }

    @Test
    @Order(8)
    void testListVoiceChannelsAsync() throws Exception {
        PagedList<VoiceChannel> channels = bot.voiceChannelsAsync().get(10, TimeUnit.SECONDS);
        assertNotNull(channels);
        assertNotNull(channels);
        System.out.println("Async voice channels: " + channels.meta().total());
    }

    @Test
    @Order(9)
    void testGetGatewayUrl() {
        String url = bot.httpClient().getGatewayUrl(true);
        assertNotNull(url);
        assertTrue(url.startsWith("wss://"), "Gateway URL should start with wss://");
        System.out.println("Gateway URL: " + url.substring(0, Math.min(80, url.length())) + "...");
    }

    @Test
    @Order(10)
    void testOnlineStatus() {
        OnlineStatus status = bot.onlineStatus();
        assertNotNull(status);
        assertNotNull(status.onlineOs());
        System.out.println("Bot online=" + status.online() + ", platforms=" + status.onlineOs());
    }

    @Test
    @Order(11)
    void testOnlineStatusAsync() throws Exception {
        OnlineStatus status = bot.onlineStatusAsync().get(10, TimeUnit.SECONDS);
        assertNotNull(status);
        assertNotNull(status.onlineOs());
        System.out.println("Async online=" + status.online() + ", platforms=" + status.onlineOs());
    }

    @Test
    @Order(12)
    void testMultipleConcurrentAsyncRequests() throws Exception {
        int n = 5;
        var futures = new ArrayList<CompletableFuture<User>>(n);
        for (int i = 0; i < n; i++) {
            futures.add(bot.meAsync());
        }

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .get(15, TimeUnit.SECONDS);

        String expectedId = bot.me().id();
        for (var f : futures) {
            assertEquals(expectedId, f.join().id(),
                    "All concurrent requests should return the same user id");
        }
        System.out.println("All " + n + " concurrent async requests completed successfully");
    }
}