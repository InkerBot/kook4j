package bot.inker.kook4j.scope;

import bot.inker.kook4j.BotInstance;
import bot.inker.kook4j.Kook4j;
import bot.inker.kook4j.entity.Role;
import bot.inker.kook4j.request.RoleUpdateRequest;
import org.junit.jupiter.api.*;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RoleScopeTest {

    private static final String TOKEN = System.getProperty("KOOK_TOKEN");

    private static BotInstance bot;
    private static String firstGuildId;

    // Roles created during tests — cleaned up in @AfterAll if tests fail mid-way
    private static Integer createdRoleId;
    private static Integer asyncCreatedRoleId;

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
        // Best-effort cleanup if a test failed before deleting
        if (createdRoleId != null) {
            try {
                bot.guild(firstGuildId).role(createdRoleId).delete();
            } catch (Exception ignored) {}
        }
        if (asyncCreatedRoleId != null) {
            try {
                bot.guild(firstGuildId).role(asyncCreatedRoleId).delete();
            } catch (Exception ignored) {}
        }
        if (bot != null) bot.stop();
    }

    @Test
    @Order(1)
    void testCreateRole() {
        Role role = bot.guild(firstGuildId).createRole("kook4j-test-role");
        assertNotNull(role);
        assertNotNull(role.name());
        assertTrue(role.roleId() > 0);
        createdRoleId = role.roleId();
        System.out.println("Created role: " + role.name() + " (id=" + role.roleId() + ")");
    }

    @Test
    @Order(2)
    void testUpdateRole() {
        assumeTrue(createdRoleId != null, "Need role from previous test");

        var req = RoleUpdateRequest.builder().name("kook4j-test-role-updated").build();
        Role updated = bot.guild(firstGuildId).role(createdRoleId).update(req);
        assertNotNull(updated);
        assertEquals("kook4j-test-role-updated", updated.name());
        System.out.println("Updated role name: " + updated.name());
    }

    @Test
    @Order(3)
    void testDeleteRole() {
        assumeTrue(createdRoleId != null, "Need role from previous test");

        bot.guild(firstGuildId).role(createdRoleId).delete();
        createdRoleId = null;
        System.out.println("Role deleted");
    }

    @Test
    @Order(4)
    void testCreateRoleAsync() throws Exception {
        Role role = bot.guild(firstGuildId).createRoleAsync("kook4j-async-role").get(10, TimeUnit.SECONDS);
        assertNotNull(role);
        assertTrue(role.roleId() > 0);
        asyncCreatedRoleId = role.roleId();
        System.out.println("Async created role: " + role.name() + " (id=" + role.roleId() + ")");
    }

    @Test
    @Order(5)
    void testUpdateRoleAsync() throws Exception {
        assumeTrue(asyncCreatedRoleId != null, "Need role from previous test");

        var req = RoleUpdateRequest.builder().name("kook4j-async-role-updated").build();
        Role updated = bot.guild(firstGuildId).role(asyncCreatedRoleId).updateAsync(req).get(10, TimeUnit.SECONDS);
        assertNotNull(updated);
        assertEquals("kook4j-async-role-updated", updated.name());
        System.out.println("Async updated role name: " + updated.name());
    }

    @Test
    @Order(6)
    void testDeleteRoleAsync() throws Exception {
        assumeTrue(asyncCreatedRoleId != null, "Need role from previous test");

        bot.guild(firstGuildId).role(asyncCreatedRoleId).deleteAsync().get(10, TimeUnit.SECONDS);
        asyncCreatedRoleId = null;
        System.out.println("Async role deleted");
    }
}