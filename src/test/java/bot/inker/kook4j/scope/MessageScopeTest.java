package bot.inker.kook4j.scope;

import bot.inker.kook4j.BotInstance;
import bot.inker.kook4j.Kook4j;
import bot.inker.kook4j.entity.*;
import bot.inker.kook4j.exception.KookApiException;
import bot.inker.kook4j.exception.KookException;
import bot.inker.kook4j.request.MessageUpdateRequest;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.*;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MessageScopeTest {

    private static final String TOKEN = System.getProperty("KOOK_TOKEN");

    private static BotInstance bot;
    private static String firstChannelId;

    @BeforeAll
    static void setup() {
        assumeTrue(TOKEN != null && !TOKEN.isBlank(), "KOOK_TOKEN not set, skipping integration tests");
        bot = Kook4j.builder().token(TOKEN).build();

        var guilds = bot.guilds();
        assumeFalse(guilds.isEmpty(), "Bot must be in at least one guild");
        String guildId = guilds.getFirst().id();

        var channels = bot.guild(guildId).listChannels();
        var textChannel = channels.stream()
                .filter(Channel::isText)
                .findFirst()
                .orElse(null);
        assumeTrue(textChannel != null, "Guild must have at least one text channel");
        firstChannelId = textChannel.id();
        System.out.println("Testing against channel: " + firstChannelId);
    }

    @AfterAll
    static void teardown() {
        if (bot != null) bot.stop();
    }

    @Test
    @Order(1)
    void testViewMessage() {
        String msgId = IntegrationTestHelper.sendAndSettle(bot, firstChannelId, "[kook4j] testViewMessage");
        try {
            Message msg = bot.message(msgId).view();
            assertNotNull(msg);
            assertNotNull(msg.content());
            System.out.println("Message content: " + msg.content());
        } finally {
            bot.message(msgId).delete();
        }
    }

    @Test
    @Order(2)
    void testViewMessageAsync() throws Exception {
        String msgId = IntegrationTestHelper.sendAndSettle(bot, firstChannelId, "[kook4j] testViewMessageAsync");
        try {
            Message msg = bot.message(msgId).viewAsync().get(10, TimeUnit.SECONDS);
            assertNotNull(msg);
            assertNotNull(msg.content());
            System.out.println("Async message content: " + msg.content());
        } finally {
            bot.message(msgId).delete();
        }
    }

    @Test
    @Order(3)
    void testUpdateMessage() {
        String msgId = IntegrationTestHelper.sendAndSettle(bot, firstChannelId, "[kook4j] original");
        try {
            bot.message(msgId).update("[kook4j] updated");
            Message updated = bot.message(msgId).view();
            assertNotNull(updated);
            assertTrue(updated.content().contains("updated"),
                    "Content should contain 'updated', got: " + updated.content());
            System.out.println("Updated content: " + updated.content());
        } finally {
            bot.message(msgId).delete();
        }
    }

    @Test
    @Order(4)
    void testUpdateMessageWithRequest() {
        String msgId = IntegrationTestHelper.sendAndSettle(bot, firstChannelId, "[kook4j] original request");
        try {
            var req = MessageUpdateRequest.builder().content("[kook4j] updated via request").build();
            bot.message(msgId).update(req);
            Message updated = bot.message(msgId).view();
            assertNotNull(updated);
            System.out.println("Updated via request: " + updated.content());
        } finally {
            bot.message(msgId).delete();
        }
    }

    @Test
    @Order(5)
    void testUpdateMessageAsync() throws Exception {
        String msgId = IntegrationTestHelper.sendAndSettle(bot, firstChannelId, "[kook4j] async original");
        try {
            bot.message(msgId).updateAsync("[kook4j] async updated").get(10, TimeUnit.SECONDS);
            Message updated = bot.message(msgId).view();
            assertNotNull(updated);
            assertTrue(updated.content().contains("async updated"),
                    "Content should contain 'async updated', got: " + updated.content());
            System.out.println("Async updated content: " + updated.content());
        } finally {
            bot.message(msgId).delete();
        }
    }

    @Test
    @Order(6)
    void testDeleteMessage() {
        String msgId = IntegrationTestHelper.sendAndSettle(bot, firstChannelId, "[kook4j] to be deleted");
        bot.message(msgId).delete();

        var ex = assertThrows(KookApiException.class, () -> bot.message(msgId).view());
        System.out.println("Delete verified: code=" + ex.code());
    }

    @Test
    @Order(7)
    void testDeleteMessageAsync() throws Exception {
        String msgId = IntegrationTestHelper.sendAndSettle(bot, firstChannelId, "[kook4j] async to be deleted");
        bot.message(msgId).deleteAsync().get(10, TimeUnit.SECONDS);

        var ex = assertThrows(KookApiException.class, () -> bot.message(msgId).view());
        assertEquals(40012, ex.code());
        System.out.println("Async delete verified: code=" + ex.code());
    }

    @Test
    @Order(8)
    void testAddAndRemoveReaction() {
        String msgId = IntegrationTestHelper.sendAndSettle(bot, firstChannelId, "[kook4j] reaction test");
        try {
            bot.message(msgId).addReaction("\uD83D\uDC4D"); // 👍
            System.out.println("Added reaction 👍");

            bot.message(msgId).removeReaction("\uD83D\uDC4D");
            System.out.println("Removed reaction 👍");
        } finally {
            bot.message(msgId).delete();
        }
    }

    @Test
    @Order(9)
    void testAddAndRemoveReactionAsync() throws Exception {
        String msgId = IntegrationTestHelper.sendAndSettle(bot, firstChannelId, "[kook4j] async reaction test");
        try {
            bot.message(msgId).addReactionAsync("\uD83D\uDC4D").get(10, TimeUnit.SECONDS);
            System.out.println("Async added reaction 👍");

            bot.message(msgId).removeReactionAsync("\uD83D\uDC4D").get(10, TimeUnit.SECONDS);
            System.out.println("Async removed reaction 👍");
        } finally {
            bot.message(msgId).delete();
        }
    }

    @Test
    @Order(10)
    void testReactionList() {
        String msgId = IntegrationTestHelper.sendAndSettle(bot, firstChannelId, "[kook4j] reaction list test");
        try {
            bot.message(msgId).addReaction("\uD83D\uDC4D"); // 👍
            var users = bot.message(msgId).reactionUsers("\uD83D\uDC4D");
            assertNotNull(users);
            assertFalse(users.isEmpty(), "Reaction list should have at least the bot");
            System.out.println("Reaction users count: " + users.size());
        } finally {
            bot.message(msgId).delete();
        }
    }

    @Test
    @Order(11)
    void testReactionListAsync() throws Exception {
        String msgId = IntegrationTestHelper.sendAndSettle(bot, firstChannelId, "[kook4j] async reaction list test");
        try {
            bot.message(msgId).addReaction("\uD83D\uDC4D");
            var users = bot.message(msgId).reactionUsersAsync("\uD83D\uDC4D").get(10, TimeUnit.SECONDS);
            assertNotNull(users);
            assertFalse(users.isEmpty());
            System.out.println("Async reaction users: " + users.size());
        } finally {
            bot.message(msgId).delete();
        }
    }

    @Test
    @Order(12)
    void testPinAndUnpin() {
        String msgId = IntegrationTestHelper.sendAndSettle(bot, firstChannelId, "[kook4j] pin test");
        try {
            bot.message(msgId).pin(firstChannelId);
            System.out.println("Pinned message: " + msgId);

            bot.message(msgId).unpin(firstChannelId);
            System.out.println("Unpinned message: " + msgId);
        } finally {
            bot.message(msgId).delete();
        }
    }

    @Test
    @Order(13)
    void testPinAndUnpinAsync() throws Exception {
        String msgId = IntegrationTestHelper.sendAndSettle(bot, firstChannelId, "[kook4j] async pin test");
        try {
            bot.message(msgId).pinAsync(firstChannelId).get(10, TimeUnit.SECONDS);
            System.out.println("Async pinned message: " + msgId);

            bot.message(msgId).unpinAsync(firstChannelId).get(10, TimeUnit.SECONDS);
            System.out.println("Async unpinned message: " + msgId);
        } finally {
            bot.message(msgId).delete();
        }
    }

    @Test
    @Order(14)
    void testSendPipeMessageWithInvalidTokenThrows() {
        var body = new JsonObject();
        body.addProperty("content", "[kook4j] pipe test");

        // Any API-level or connection exception is acceptable here; what matters is that the
        // error originates from the server (the HTTP call was made), not from bad local code.
        assertThrows(KookException.class, () ->
                bot.message("ignored").sendPipeMessage("invalid-pipeline-token", body));
        System.out.println("sendPipeMessage correctly raised KookException for invalid token");
    }

    @Test
    @Order(15)
    void testSendPipeMessageAsyncWithInvalidTokenThrows() {
        var body = new JsonObject();
        body.addProperty("content", "[kook4j] async pipe test");

        var future = bot.message("ignored").sendPipeMessageAsync("invalid-pipeline-token", body);
        assertThrows(Exception.class, () -> future.get(10, TimeUnit.SECONDS));
        System.out.println("Async sendPipeMessage correctly raised exception for invalid token");
    }
}