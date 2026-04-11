package bot.inker.kook4j.scope;

import bot.inker.kook4j.BotInstance;
import bot.inker.kook4j.Kook4j;
import bot.inker.kook4j.entity.*;
import org.junit.jupiter.api.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GuildScopeTest {

    private static final String TOKEN = System.getProperty("KOOK_TOKEN");

    private static BotInstance bot;
    private static String firstGuildId;

    @BeforeAll
    static void setup() {
        assumeTrue(TOKEN != null && !TOKEN.isBlank(), "KOOK_TOKEN not set, skipping integration tests");
        bot = Kook4j.builder().token(TOKEN).build();
        var guilds = bot.guilds();
        assumeFalse(guilds.isEmpty(), "Bot must be in at least one guild");
        firstGuildId = guilds.getFirst().id();
        System.out.println("Testing against guild: " + firstGuildId);
    }

    @AfterAll
    static void teardown() {
        if (bot != null) bot.stop();
    }

    @Test
    @Order(1)
    void testViewGuild() {
        Guild guild = bot.guild(firstGuildId).view();
        assertNotNull(guild);
        assertEquals(firstGuildId, guild.id());
        assertNotNull(guild.name());
        System.out.println("Guild: " + guild.name() + ", owner=" + guild.userId());
    }

    @Test
    @Order(2)
    void testViewGuildAsync() throws Exception {
        Guild guild = bot.guild(firstGuildId).viewAsync().get(10, TimeUnit.SECONDS);
        assertNotNull(guild);
        assertEquals(firstGuildId, guild.id());
        assertNotNull(guild.name());
        System.out.println("Async guild name: " + guild.name());
    }

    @Test
    @Order(3)
    void testListChannels() {
        PagedList<Channel> channels = bot.guild(firstGuildId).listChannels();
        assertNotNull(channels);
        assertFalse(channels.isEmpty(), "Guild should have at least one channel");
        System.out.println("Found " + channels.meta().total() + " channels");
    }

    @Test
    @Order(4)
    void testListChannelsAsync() throws Exception {
        PagedList<Channel> channels = bot.guild(firstGuildId).listChannelsAsync().get(10, TimeUnit.SECONDS);
        assertNotNull(channels);
        assertFalse(channels.isEmpty());
        System.out.println("Async channels: " + channels.meta().total());
    }

    @Test
    @Order(5)
    void testListGuildMembers() {
        PagedList<User> members = bot.guild(firstGuildId).members();
        assertNotNull(members);
        assertFalse(members.isEmpty());
        System.out.println("Guild has " + members.meta().total() + " members");
    }

    @Test
    @Order(6)
    void testListGuildMembersAsync() throws Exception {
        PagedList<User> members = bot.guild(firstGuildId).membersAsync().get(10, TimeUnit.SECONDS);
        assertNotNull(members);
        assertFalse(members.isEmpty());
        System.out.println("Async members: " + members.meta().total());
    }

    @Test
    @Order(7)
    void testPaginationPageSize1() {
        PagedList<User> page1 = bot.guild(firstGuildId).members(1, 1);
        assertNotNull(page1);
        assertEquals(1, page1.size());
        assertTrue(page1.meta().total() > 0);
        System.out.println("Page 1 (size=1): member=" + page1.getFirst().username()
                + ", total=" + page1.meta().total());
    }

    @Test
    @Order(8)
    void testPaginationPageSize1Async() throws Exception {
        PagedList<User> page1 = bot.guild(firstGuildId).membersAsync(1, 1).get(10, TimeUnit.SECONDS);
        assertNotNull(page1);
        assertEquals(1, page1.size());
        assertTrue(page1.meta().total() > 0);
        System.out.println("Async page 1 (size=1): total=" + page1.meta().total());
    }

    @Test
    @Order(9)
    void testListGuildRoles() {
        PagedList<Role> roles = bot.guild(firstGuildId).listRoles();
        assertNotNull(roles);
        System.out.println("Guild has " + roles.meta().total() + " roles");
        for (Role role : roles) {
            System.out.println("  Role: " + role.name() + " (id=" + role.roleId() + ")");
        }
    }

    @Test
    @Order(10)
    void testListGuildRolesAsync() throws Exception {
        PagedList<Role> roles = bot.guild(firstGuildId).listRolesAsync().get(10, TimeUnit.SECONDS);
        assertNotNull(roles);
        assertFalse(roles.isEmpty());
        System.out.println("Async guild roles: " + roles.meta().total());
    }

    @Test
    @Order(11)
    void testListGuildEmojis() {
        PagedList<GuildEmoji> emojis = bot.guild(firstGuildId).listEmojis();
        assertNotNull(emojis);
        System.out.println("Guild has " + emojis.meta().total() + " custom emojis");
    }

    @Test
    @Order(12)
    void testListGuildEmojisAsync() throws Exception {
        PagedList<GuildEmoji> emojis = bot.guild(firstGuildId).listEmojisAsync().get(10, TimeUnit.SECONDS);
        assertNotNull(emojis);
        System.out.println("Async emojis: " + emojis.meta().total());
    }

    @Test
    @Order(13)
    void testListGuildInvites() {
        PagedList<Invite> invites = bot.guild(firstGuildId).listInvites();
        assertNotNull(invites);
        System.out.println("Guild has " + invites.meta().total() + " active invites");
    }

    @Test
    @Order(14)
    void testListGuildInvitesAsync() throws Exception {
        PagedList<Invite> invites = bot.guild(firstGuildId).listInvitesAsync().get(10, TimeUnit.SECONDS);
        assertNotNull(invites);
        System.out.println("Async invites: " + invites.meta().total());
    }

    @Test
    @Order(15)
    void testCreateAndDeleteInvite() {
        Invite invite = bot.guild(firstGuildId).createInvite();
        assertNotNull(invite);
        assertNotNull(invite.url());
        System.out.println("Created invite: " + invite.url());
    }

    @Test
    @Order(16)
    void testCreateAndDeleteInviteAsync() throws Exception {
        Invite invite = bot.guild(firstGuildId).createInviteAsync().get(10, TimeUnit.SECONDS);
        assertNotNull(invite);
        assertNotNull(invite.url());
        System.out.println("Async created invite: " + invite.url());
    }

    @Test
    @Order(17)
    void testMuteList() {
        GuildMuteList muteList = bot.guild(firstGuildId).muteList();
        assertNotNull(muteList);
        assertNotNull(muteList.mic());
        assertNotNull(muteList.headset());
        assertNotNull(muteList.mic().userIds());
        assertNotNull(muteList.headset().userIds());
        System.out.println("Muted mic=" + muteList.mic().userIds().size()
                + ", headset=" + muteList.headset().userIds().size());
    }

    @Test
    @Order(18)
    void testMuteListAsync() throws Exception {
        GuildMuteList muteList = bot.guild(firstGuildId).muteListAsync().get(10, TimeUnit.SECONDS);
        assertNotNull(muteList);
        assertNotNull(muteList.mic());
        assertNotNull(muteList.headset());
        System.out.println("Async muted mic=" + muteList.mic().userIds().size()
                + ", headset=" + muteList.headset().userIds().size());
    }

    @Test
    @Order(19)
    void testBoostHistory() {
        PagedList<GuildBoostRecord> history = bot.guild(firstGuildId).boostHistory();
        assertNotNull(history);
        System.out.println("Boost history: " + history.meta().total() + " records");
        history.forEach(r ->
                System.out.println("  boost userId=" + r.userId()
                        + " start=" + r.startTime() + " end=" + r.endTime()));
    }

    @Test
    @Order(20)
    void testBoostHistoryAsync() throws Exception {
        PagedList<GuildBoostRecord> history =
                bot.guild(firstGuildId).boostHistoryAsync().get(10, TimeUnit.SECONDS);
        assertNotNull(history);
        System.out.println("Async boost history: " + history.meta().total() + " records");
    }

    @Test
    @Order(21)
    void testBoostHistoryWithTimeRange() {
        // empty range (start > end) should return empty list without throwing
        long now = System.currentTimeMillis() / 1000;
        PagedList<GuildBoostRecord> history =
                bot.guild(firstGuildId).boostHistory(now + 86400, now + 172800);
        assertNotNull(history);
        System.out.println("Future-range boost history: " + history.meta().total() + " records (expect 0)");
    }

    @Test
    @Order(22)
    void testBadge() {
        byte[] png = bot.guild(firstGuildId).badge(0);
        assertNotNull(png);
        assertTrue(png.length > 0, "Badge image should be non-empty");
        // Badge API returns SVG; verify it starts with '<'
        assertEquals((byte) '<', png[0]);
        System.out.println("Badge SVG size: " + png.length + " bytes");
    }

    @Test
    @Order(23)
    void testBadgeStyles() throws Exception {
        for (int style = 0; style <= 2; style++) {
            byte[] png = bot.guild(firstGuildId).badgeAsync(style).get(10, TimeUnit.SECONDS);
            assertNotNull(png);
            assertTrue(png.length > 0, "Badge style=" + style + " should be non-empty");
            System.out.println("Badge style=" + style + " size=" + png.length + " bytes");
        }
    }

    @Test
    @Order(24)
    void testEmojiLifecycle() throws Exception {
        // Build a minimal 32×32 PNG in a temp file
        var img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
        File tmpPng = File.createTempFile("kook4j-emoji-", ".png");
        tmpPng.deleteOnExit();
        ImageIO.write(img, "png", tmpPng);

        GuildEmoji created = bot.guild(firstGuildId).createEmoji("kook4jtest", tmpPng);
        assertNotNull(created);
        assertNotNull(created.id());
        System.out.println("Created emoji id=" + created.id());

        bot.guild(firstGuildId).updateEmoji(created.id(), "kook4jrenamed");
        System.out.println("Updated emoji name");

        bot.guild(firstGuildId).deleteEmoji(created.id());
        System.out.println("Deleted emoji");
    }

    @Test
    @Order(25)
    void testEmojiLifecycleAsync() throws Exception {
        var img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
        File tmpPng = File.createTempFile("kook4j-emoji-async-", ".png");
        tmpPng.deleteOnExit();
        ImageIO.write(img, "png", tmpPng);

        GuildEmoji created = bot.guild(firstGuildId).createEmojiAsync("kook4jtestasync", tmpPng)
                .get(10, TimeUnit.SECONDS);
        assertNotNull(created);
        assertNotNull(created.id());
        System.out.println("Async created emoji id=" + created.id());

        bot.guild(firstGuildId).updateEmojiAsync(created.id(), "kook4jrenamedAsync")
                .get(10, TimeUnit.SECONDS);
        System.out.println("Async updated emoji name");

        bot.guild(firstGuildId).deleteEmojiAsync(created.id()).get(10, TimeUnit.SECONDS);
        System.out.println("Async deleted emoji");
    }

    @Test
    @Order(26)
    void testDeleteInvite() {
        Invite invite = bot.guild(firstGuildId).createInvite();
        assertNotNull(invite.url());
        String urlCode = invite.url().substring(invite.url().lastIndexOf('/') + 1);
        assertFalse(urlCode.isEmpty(), "url_code extracted from url must not be empty");

        bot.guild(firstGuildId).deleteInvite(urlCode);
        System.out.println("Deleted invite url_code=" + urlCode);
    }

    @Test
    @Order(27)
    void testDeleteInviteAsync() throws Exception {
        Invite invite = bot.guild(firstGuildId).createInviteAsync().get(10, TimeUnit.SECONDS);
        assertNotNull(invite.url());
        String urlCode = invite.url().substring(invite.url().lastIndexOf('/') + 1);
        assertFalse(urlCode.isEmpty());

        bot.guild(firstGuildId).deleteInviteAsync(urlCode).get(10, TimeUnit.SECONDS);
        System.out.println("Async deleted invite url_code=" + urlCode);
    }

    @Test
    @Order(28)
    void testJoinedChannel() {
        // Bot itself is unlikely to be in a voice channel; we just verify the API responds
        User me = bot.me();
        PagedList<Channel> joined = bot.guild(firstGuildId).getJoinedChannel(me.id());
        assertNotNull(joined);
        System.out.println("Bot joined voice channels: " + joined.meta().total());
    }

    @Test
    @Order(29)
    void testJoinedChannelAsync() throws Exception {
        User me = bot.me();
        PagedList<Channel> joined = bot.guild(firstGuildId).getJoinedChannelAsync(me.id())
                .get(10, TimeUnit.SECONDS);
        assertNotNull(joined);
        System.out.println("Async bot joined voice channels: " + joined.meta().total());
    }
}