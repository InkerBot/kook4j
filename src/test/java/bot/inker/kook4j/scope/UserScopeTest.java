package bot.inker.kook4j.scope;

import bot.inker.kook4j.BotInstance;
import bot.inker.kook4j.Kook4j;
import bot.inker.kook4j.entity.User;
import org.junit.jupiter.api.*;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserScopeTest {

    private static final String TOKEN = System.getProperty("KOOK_TOKEN");

    private static BotInstance bot;
    private static String botUserId;
    private static String firstGuildId;

    @BeforeAll
    static void setup() {
        assumeTrue(TOKEN != null && !TOKEN.isBlank(), "KOOK_TOKEN not set, skipping integration tests");
        bot = Kook4j.builder().token(TOKEN).build();
        botUserId = bot.me().id();

        var guilds = bot.guilds();
        assumeFalse(guilds.isEmpty(), "Bot must be in at least one guild");
        firstGuildId = guilds.getFirst().id();
        System.out.println("Testing against user: " + botUserId + ", guild: " + firstGuildId);
    }

    @AfterAll
    static void teardown() {
        if (bot != null) bot.stop();
    }

    @Test
    @Order(1)
    void testViewUser() {
        User user = bot.user(botUserId).view();
        assertNotNull(user);
        assertEquals(botUserId, user.id());
        assertNotNull(user.username());
        System.out.println("User: " + user.username() + " (id=" + user.id() + ")");
    }

    @Test
    @Order(2)
    void testViewUserWithGuildContext() {
        User user = bot.user(botUserId).view(firstGuildId);
        assertNotNull(user);
        assertEquals(botUserId, user.id());
        assertNotNull(user.username());
        System.out.println("User in guild: " + user.username());
    }

    @Test
    @Order(3)
    void testViewUserAsync() throws Exception {
        User user = bot.user(botUserId).viewAsync().get(10, TimeUnit.SECONDS);
        assertNotNull(user);
        assertEquals(botUserId, user.id());
        System.out.println("Async user: " + user.username());
    }

    @Test
    @Order(4)
    void testViewUserWithGuildContextAsync() throws Exception {
        User user = bot.user(botUserId).viewAsync(firstGuildId).get(10, TimeUnit.SECONDS);
        assertNotNull(user);
        assertEquals(botUserId, user.id());
        System.out.println("Async user in guild: " + user.username());
    }

}