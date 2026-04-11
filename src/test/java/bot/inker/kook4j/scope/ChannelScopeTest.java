package bot.inker.kook4j.scope;

import bot.inker.kook4j.BotInstance;
import bot.inker.kook4j.Kook4j;
import bot.inker.kook4j.entity.*;
import bot.inker.kook4j.exception.KookApiException;
import bot.inker.kook4j.request.MessageCreateRequest;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ChannelScopeTest {

    private static final String TOKEN = System.getProperty("KOOK_TOKEN");

    private static BotInstance bot;
    private static String firstGuildId;
    private static String firstChannelId;

    @BeforeAll
    static void setup() {
        assumeTrue(TOKEN != null && !TOKEN.isBlank(), "KOOK_TOKEN not set, skipping integration tests");
        bot = Kook4j.builder().token(TOKEN).build();

        var guilds = bot.guilds();
        assumeFalse(guilds.isEmpty(), "Bot must be in at least one guild");
        firstGuildId = guilds.getFirst().id();

        var channels = bot.guild(firstGuildId).listChannels();
        var textChannel = channels.stream()
                .filter(Channel::isText)
                .findFirst()
                .orElse(null);
        assumeTrue(textChannel != null, "Guild must have at least one text channel");
        firstChannelId = textChannel.id();
        System.out.println("Testing against channel: " + firstChannelId);

        // Pick an arbitrary role for channel-role tests (prefer non-@everyone)
        var roles = bot.guild(firstGuildId).listRoles();
        if (!roles.isEmpty()) {
            testRoleId = roles.stream()
                    .mapToInt(Role::roleId)
                    .filter(id -> id != 0)
                    .findFirst()
                    .orElse(roles.getFirst().roleId());
            System.out.println("Using testRoleId=" + testRoleId + " for channel-role tests");
        }
    }

    // role id used for channel-role lifecycle tests — resolved once in setup
    private static Integer testRoleId;

    @AfterAll
    static void teardown() {
        if (bot != null) bot.stop();
    }

    @Test
    @Order(1)
    void testViewChannel() {
        Channel channel = bot.channel(firstChannelId).view();
        assertNotNull(channel);
        assertEquals(firstChannelId, channel.id());
        assertNotNull(channel.name());
        System.out.println("Channel: " + channel.name() + ", type=" + channel.type());
    }

    @Test
    @Order(2)
    void testViewChannelAsync() throws Exception {
        Channel channel = bot.channel(firstChannelId).viewAsync().get(10, TimeUnit.SECONDS);
        assertNotNull(channel);
        assertEquals(firstChannelId, channel.id());
        System.out.println("Async channel name: " + channel.name());
    }

    @Test
    @Order(3)
    void testViewChannelWithChildren() {
        Channel channel = bot.channel(firstChannelId).view(true);
        assertNotNull(channel);
        assertEquals(firstChannelId, channel.id());
        System.out.println("Channel with children: " + channel.name());
    }

    @Test
    @Order(4)
    void testListChannelMessages() {
        List<Message> messages = bot.channel(firstChannelId).messages();
        assertNotNull(messages);
        System.out.println("Channel has " + messages.size() + " recent messages");
    }

    @Test
    @Order(5)
    void testListChannelMessagesAsync() throws Exception {
        List<Message> messages = bot.channel(firstChannelId).messagesAsync().get(10, TimeUnit.SECONDS);
        assertNotNull(messages);
        System.out.println("Async channel messages: " + messages.size());
    }

    @Test
    @Order(6)
    void testSendAndDeleteMessage() {
        String msgId = IntegrationTestHelper.sendAndSettle(bot, firstChannelId,
                "[kook4j integration test] This message will be deleted shortly.");
        assertNotNull(msgId);
        assertFalse(msgId.isEmpty());
        System.out.println("Sent message: " + msgId);

        bot.message(msgId).delete();
        System.out.println("Message deleted");

        var ex = assertThrows(KookApiException.class, () -> bot.message(msgId).view());
        System.out.println("Delete verified: code=" + ex.code());
    }

    @Test
    @Order(7)
    void testSendAndDeleteMessageAsync() throws Exception {
        String msgId = bot.channel(firstChannelId)
                .sendAsync("[kook4j async test] This message will be deleted.")
                .get(10, TimeUnit.SECONDS);
        IntegrationTestHelper.settle(bot, msgId);
        assertNotNull(msgId);
        assertFalse(msgId.isEmpty());
        System.out.println("Async sent message: " + msgId);

        bot.message(msgId).deleteAsync().get(10, TimeUnit.SECONDS);
        System.out.println("Async message deleted");

        var ex = assertThrows(KookApiException.class, () -> bot.message(msgId).view());
        assertEquals(40012, ex.code());
    }

    @Test
    @Order(8)
    void testSendAndUpdateMessage() {
        String msgId = IntegrationTestHelper.sendAndSettle(bot, firstChannelId,
                "[kook4j update test] Original text.");
        assertNotNull(msgId);

        bot.message(msgId).update("[kook4j update test] Updated text.");

        Message updated = bot.message(msgId).view();
        assertNotNull(updated);
        assertTrue(updated.content().contains("Updated text"),
                "Content should be updated, got: " + updated.content());
        System.out.println("Updated message content: " + updated.content());

        bot.message(msgId).delete();
    }

    @Test
    @Order(9)
    void testSendAndUpdateMessageAsync() throws Exception {
        String msgId = bot.channel(firstChannelId)
                .sendAsync("[kook4j async update test] Original text.")
                .get(10, TimeUnit.SECONDS);
        IntegrationTestHelper.settle(bot, msgId);
        assertNotNull(msgId);

        bot.message(msgId).updateAsync("[kook4j async update test] Updated text.").get(10, TimeUnit.SECONDS);

        Message updated = bot.message(msgId).view();
        assertNotNull(updated);
        assertTrue(updated.content().contains("Updated text"),
                "Content should be updated, got: " + updated.content());
        System.out.println("Async updated message content: " + updated.content());

        bot.message(msgId).delete();
    }

    @Test
    @Order(10)
    void testSendWithKMarkdownRequest() {
        var request = MessageCreateRequest.builder()
                .content("**bold** and *italic* from kook4j test")
                .build();
        String msgId = bot.channel(firstChannelId).send(request);
        IntegrationTestHelper.settle(bot, msgId);
        assertNotNull(msgId);
        System.out.println("KMarkdown message sent: " + msgId);

        bot.message(msgId).delete();
    }

    @Test
    @Order(11)
    void testSendWithKMarkdownRequestAsync() throws Exception {
        var request = MessageCreateRequest.builder()
                .content("**async bold** from kook4j test")
                .build();
        String msgId = bot.channel(firstChannelId).sendAsync(request).get(10, TimeUnit.SECONDS);
        IntegrationTestHelper.settle(bot, msgId);
        assertNotNull(msgId);
        System.out.println("Async KMarkdown message sent: " + msgId);

        bot.message(msgId).deleteAsync().get(10, TimeUnit.SECONDS);
    }

    @Test
    @Order(12)
    void testRolePermissions() {
        ChannelRolePermissions perms = bot.channel(firstChannelId).rolePermissions();
        assertNotNull(perms);
        assertNotNull(perms.permissionOverwrites());
        assertNotNull(perms.permissionUsers());
        System.out.println("Role overrides=" + perms.permissionOverwrites().size()
                + " userOverrides=" + perms.permissionUsers().size()
                + " sync=" + perms.permissionSync());
    }

    @Test
    @Order(13)
    void testRolePermissionsAsync() throws Exception {
        ChannelRolePermissions perms = bot.channel(firstChannelId).rolePermissionsAsync()
                .get(10, TimeUnit.SECONDS);
        assertNotNull(perms);
        assertNotNull(perms.permissionOverwrites());
        System.out.println("Async role overrides=" + perms.permissionOverwrites().size());
    }

    @Test
    @Order(14)
    void testChannelRolePermissionLifecycle() {
        assumeTrue(testRoleId != null, "Need at least one role in the guild");

        String roleIdStr = String.valueOf(testRoleId);

        ChannelRoleEntry created = bot.channel(firstChannelId)
                .createRolePermission("role_id", roleIdStr);
        assertNotNull(created);
        System.out.println("Created channel-role entry: roleId=" + created.roleId()
                + " allow=" + created.allow() + " deny=" + created.deny());

        ChannelRoleEntry updated = bot.channel(firstChannelId)
                .updateRolePermission("role_id", roleIdStr, 2048, 0);
        assertNotNull(updated);
        assertEquals(2048, updated.allow());
        assertEquals(0,    updated.deny());
        System.out.println("Updated channel-role entry: allow=" + updated.allow());

        bot.channel(firstChannelId).deleteRolePermission("role_id", roleIdStr);
        System.out.println("Deleted channel-role entry for roleId=" + roleIdStr);
    }

    @Test
    @Order(15)
    void testChannelRolePermissionLifecycleAsync() throws Exception {
        assumeTrue(testRoleId != null, "Need at least one role in the guild");

        String roleIdStr = String.valueOf(testRoleId);

        ChannelRoleEntry created = bot.channel(firstChannelId)
                .createRolePermissionAsync("role_id", roleIdStr).get(10, TimeUnit.SECONDS);
        assertNotNull(created);
        System.out.println("Async created channel-role entry: roleId=" + created.roleId());

        ChannelRoleEntry updated = bot.channel(firstChannelId)
                .updateRolePermissionAsync("role_id", roleIdStr, 1024, 0).get(10, TimeUnit.SECONDS);
        assertNotNull(updated);
        assertEquals(1024, updated.allow());
        System.out.println("Async updated channel-role entry: allow=" + updated.allow());

        bot.channel(firstChannelId).deleteRolePermissionAsync("role_id", roleIdStr)
                .get(10, TimeUnit.SECONDS);
        System.out.println("Async deleted channel-role entry");
    }

    @Test
    @Order(16)
    void testSyncRolePermissions() {
        // Sync only makes sense for channels that belong to a category (parentId != empty)
        Channel ch = bot.channel(firstChannelId).view();
        assumeTrue(ch.parentId() != null && !ch.parentId().isEmpty(),
                "Channel must be in a category to test sync");

        ChannelRolePermissions synced = bot.channel(firstChannelId).syncRolePermissions();
        assertNotNull(synced);
        assertNotNull(synced.permissionOverwrites());
        assertNotNull(synced.permissionUsers());
        System.out.println("Synced channel role permissions: overwrites="
                + synced.permissionOverwrites().size());
    }

    @Test
    @Order(17)
    void testSyncRolePermissionsAsync() throws Exception {
        Channel ch = bot.channel(firstChannelId).view();
        assumeTrue(ch.parentId() != null && !ch.parentId().isEmpty(),
                "Channel must be in a category to test sync");

        ChannelRolePermissions synced = bot.channel(firstChannelId).syncRolePermissionsAsync()
                .get(10, TimeUnit.SECONDS);
        assertNotNull(synced);
        System.out.println("Async synced channel role permissions: overwrites="
                + synced.permissionOverwrites().size());
    }
}