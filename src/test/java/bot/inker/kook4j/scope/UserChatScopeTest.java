package bot.inker.kook4j.scope;

import bot.inker.kook4j.BotInstance;
import bot.inker.kook4j.Kook4j;
import bot.inker.kook4j.entity.Message;
import bot.inker.kook4j.entity.UserChat;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserChatScopeTest {

    private static final String TOKEN = System.getProperty("KOOK_TOKEN");

    private static BotInstance bot;
    private static String firstChatCode;

    @BeforeAll
    static void setup() {
        assumeTrue(TOKEN != null && !TOKEN.isBlank(), "KOOK_TOKEN not set, skipping integration tests");
        bot = Kook4j.builder().token(TOKEN).build();

        var chats = bot.userChats();
        assumeFalse(chats.isEmpty(), "Bot must have at least one DM session for UserChatScope tests");
        firstChatCode = chats.getFirst().code();
        System.out.println("Testing against chat: " + firstChatCode);
    }

    @AfterAll
    static void teardown() {
        if (bot != null) bot.stop();
    }

    @Test
    @Order(1)
    void testViewUserChat() {
        UserChat chat = bot.userChat(firstChatCode).view();
        assertNotNull(chat);
        assertEquals(firstChatCode, chat.code());
        assertNotNull(chat.targetInfo());
        System.out.println("Chat target: " + chat.targetInfo().username());
    }

    @Test
    @Order(2)
    void testViewUserChatAsync() throws Exception {
        UserChat chat = bot.userChat(firstChatCode).viewAsync().get(10, TimeUnit.SECONDS);
        assertNotNull(chat);
        assertEquals(firstChatCode, chat.code());
        System.out.println("Async chat target: " + chat.targetInfo().username());
    }

    @Test
    @Order(3)
    void testListUserChatMessages() {
        List<Message> messages = bot.userChat(firstChatCode).messages();
        assertNotNull(messages);
        System.out.println("DM chat has " + messages.size() + " recent messages");
    }

    @Test
    @Order(4)
    void testListUserChatMessagesAsync() throws Exception {
        List<Message> messages = bot.userChat(firstChatCode).messagesAsync().get(10, TimeUnit.SECONDS);
        assertNotNull(messages);
        System.out.println("Async DM messages: " + messages.size());
    }
}