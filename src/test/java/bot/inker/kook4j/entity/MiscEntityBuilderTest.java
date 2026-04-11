package bot.inker.kook4j.entity;

import bot.inker.kook4j.Kook4jCodec;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MiscEntityBuilderTest {

    @Test
    void guildEmoji_builder_setsFields() {
        var emoji = GuildEmoji.builder()
                .id("emoji-1")
                .name("cool_face")
                .guildId("g-1")
                .userId("u-1")
                .build();

        assertEquals("emoji-1", emoji.id());
        assertEquals("cool_face", emoji.name());
        assertEquals("g-1", emoji.guildId());
        assertEquals("u-1", emoji.userId());
    }

    @Test
    void invite_builder_setsFields() {
        var user = User.builder().id("u-1").build();
        var invite = Invite.builder()
                .urlCode("ABC123")
                .url("https://kook.top/ABC123")
                .guildId("g-1")
                .channelId("ch-1")
                .user(user)
                .duration(7)
                .usingTimes(3)
                .remainingTimes(4)
                .build();

        assertEquals("ABC123", invite.urlCode());
        assertEquals("https://kook.top/ABC123", invite.url());
        assertEquals(7, invite.duration());
        assertEquals(3, invite.usingTimes());
        assertEquals(4, invite.remainingTimes());
    }

    @Test
    void userChat_builder_setsFields() {
        var target = User.builder().id("u-2").username("Charlie").build();
        var chat = UserChat.builder()
                .code("chat-code-1")
                .targetInfo(target)
                .unreadCount(5)
                .isFriend(true)
                .isBlocked(false)
                .build();

        assertEquals("chat-code-1", chat.code());
        assertEquals("u-2", chat.targetInfo().id());
        assertEquals(5, chat.unreadCount());
        assertTrue(chat.isFriend());
        assertFalse(chat.isBlocked());
    }

    @Test
    void voiceChannel_builder_setsFields() {
        var vc = VoiceChannel.builder()
                .id("vc-1")
                .name("General Voice")
                .guildId("g-1")
                .parentId("cat-1")
                .build();

        assertEquals("vc-1", vc.id());
        assertEquals("General Voice", vc.name());
        assertEquals("g-1", vc.guildId());
        assertEquals("cat-1", vc.parentId());
    }

    @Test
    void quote_builder_setsFields() {
        var author = User.builder().id("u-1").build();
        var quote = Quote.builder()
                .id("q-1")
                .type(9)
                .content("quoted text")
                .author(author)
                .createAt(System.currentTimeMillis())
                .build();

        assertEquals("q-1", quote.id());
        assertEquals("quoted text", quote.content());
        assertNotNull(quote.author());
    }

    @Test
    void channelRolePermissions_deserializesFromJson() {
        var json = """
                {
                  "permission_overwrites": [
                    {"role_id": 7, "allow": 1024, "deny": 8},
                    {"role_id": 0, "allow": 0,    "deny": 0}
                  ],
                  "permission_users": [
                    {"user": {"id": "u-1", "username": "Alice"}, "allow": 2048, "deny": 0}
                  ],
                  "permission_sync": 0
                }
                """;
        var perms = Kook4jCodec.fromJson(json, ChannelRolePermissions.class);

        assertNotNull(perms);
        assertEquals(2, perms.permissionOverwrites().size());
        assertEquals(7,    perms.permissionOverwrites().get(0).roleId());
        assertEquals(1024, perms.permissionOverwrites().get(0).allow());
        assertEquals(8,    perms.permissionOverwrites().get(0).deny());
        assertEquals(1, perms.permissionUsers().size());
        assertEquals("u-1", perms.permissionUsers().get(0).user().id());
        assertEquals(2048, perms.permissionUsers().get(0).allow());
        assertEquals(0, perms.permissionSync());
    }

    @Test
    void channelRoleEntry_deserializesUserId() {
        var json = """
                {"user_id": "2418200000", "allow": 2048, "deny": 0}
                """;
        var entry = Kook4jCodec.fromJson(json, ChannelRoleEntry.class);

        assertNotNull(entry);
        assertEquals("2418200000", entry.userId());
        assertNull(entry.roleId());
        assertEquals(2048, entry.allow());
        assertEquals(0, entry.deny());
    }

    @Test
    void channelRoleEntry_deserializesRoleId() {
        var json = """
                {"role_id": 702, "allow": 4096, "deny": 128}
                """;
        var entry = Kook4jCodec.fromJson(json, ChannelRoleEntry.class);

        assertNotNull(entry);
        assertNull(entry.userId());
        assertEquals(702, entry.roleId());
        assertEquals(4096, entry.allow());
        assertEquals(128, entry.deny());
    }

    @Test
    void guildMuteList_deserializesFromJson() {
        var json = """
                {
                  "mic":     {"type": 1, "user_ids": ["u-1", "u-2"]},
                  "headset": {"type": 2, "user_ids": ["u-3"]}
                }
                """;
        var muteList = Kook4jCodec.fromJson(json, GuildMuteList.class);

        assertNotNull(muteList);
        assertNotNull(muteList.mic());
        assertEquals(1, muteList.mic().type());
        assertEquals(List.of("u-1", "u-2"), muteList.mic().userIds());
        assertNotNull(muteList.headset());
        assertEquals(2, muteList.headset().type());
        assertEquals(List.of("u-3"), muteList.headset().userIds());
    }

    @Test
    void guildMuteList_emptyGroups() {
        var json = """
                {
                  "mic":     {"type": 1, "user_ids": []},
                  "headset": {"type": 2, "user_ids": []}
                }
                """;
        var muteList = Kook4jCodec.fromJson(json, GuildMuteList.class);

        assertNotNull(muteList);
        assertTrue(muteList.mic().userIds().isEmpty());
        assertTrue(muteList.headset().userIds().isEmpty());
    }

    @Test
    void guildBoostRecord_deserializesFromJson() {
        var json = """
                {
                  "user_id":   "0000000000",
                  "guild_id":  "0000000000000000",
                  "start_time": 1667814426,
                  "end_time":   1670406426,
                  "user": {"id": "0000000000", "username": "Booster"}
                }
                """;
        var record = Kook4jCodec.fromJson(json, GuildBoostRecord.class);

        assertNotNull(record);
        assertEquals("0000000000",       record.userId());
        assertEquals("0000000000000000", record.guildId());
        assertEquals(1667814426L,        record.startTime());
        assertEquals(1670406426L,        record.endTime());
        assertNotNull(record.user());
        assertEquals("Booster",          record.user().username());
    }

    @Test
    void onlineStatus_deserializesFromJson() {
        var json = """
                {"online": true, "online_os": ["Webhook", "Websocket"]}
                """;
        var status = Kook4jCodec.fromJson(json, OnlineStatus.class);

        assertNotNull(status);
        assertTrue(status.online());
        assertEquals(List.of("Webhook", "Websocket"), status.onlineOs());
    }

    @Test
    void onlineStatus_offline() {
        var json = """
                {"online": false, "online_os": []}
                """;
        var status = Kook4jCodec.fromJson(json, OnlineStatus.class);

        assertNotNull(status);
        assertFalse(status.online());
        assertTrue(status.onlineOs().isEmpty());
    }

    @Test
    void attachment_builder_setsFields() {
        var att = Attachment.builder()
                .type("image")
                .url("https://img.example.com/photo.png")
                .name("photo.png")
                .fileType("image/png")
                .size(204800L)
                .width(1920)
                .height(1080)
                .build();

        assertEquals("image", att.type());
        assertEquals("https://img.example.com/photo.png", att.url());
        assertEquals(204800L, att.size());
        assertEquals(1920, att.width());
        assertEquals(1080, att.height());
    }
}
