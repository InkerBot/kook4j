package bot.inker.kook4j.entity;

import bot.inker.kook4j.Kook4jCodec;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleBuilderTest {

    @Test
    void builder_setsAllFields() {
        var role = Role.builder()
                .roleId(42)
                .name("Moderator")
                .color(0xFF5733)
                .position(3)
                .permissions(1073741823)
                .hoist(1)
                .mentionable(1)
                .build();

        assertEquals(42, role.roleId());
        assertEquals("Moderator", role.name());
        assertEquals(0xFF5733, role.color());
        assertEquals(3, role.position());
        assertEquals(1073741823, role.permissions());
    }

    @Test
    void hasPermission_checksCorrectBit() {
        // hasPermission(n) checks whether bit index n is set: (permissions & (1 << n)) != 0
        var role = Role.builder().permissions(0b1010).build();
        assertTrue(role.hasPermission(1));    // bit index 1 is set  (0b0010)
        assertTrue(role.hasPermission(3));    // bit index 3 is set  (0b1000)
        assertFalse(role.hasPermission(0));   // bit index 0 not set (0b0001)
        assertFalse(role.hasPermission(2));   // bit index 2 not set (0b0100)
    }

    @Test
    void isAdmin_trueWhenBitIndex0Set() {
        // isAdmin() == hasPermission(0) == bit index 0 set == (permissions & 1) != 0
        assertTrue(Role.builder().permissions(1).build().isAdmin());   // 0b0001
        assertFalse(Role.builder().permissions(2).build().isAdmin());  // 0b0010
        assertTrue(Role.builder().permissions(3).build().isAdmin());   // 0b0011 — both bits set
    }

    @Test
    void jsonRoundTrip_preservesFields() {
        var original = Role.builder()
                .roleId(99)
                .name("Tester")
                .permissions(0b11110000)
                .color(0xAABBCC)
                .build();

        var json    = Kook4jCodec.toJson(original);
        var decoded = Kook4jCodec.fromJson(json, Role.class);

        assertEquals(original.roleId(), decoded.roleId());
        assertEquals(original.name(), decoded.name());
        assertEquals(original.permissions(), decoded.permissions());
    }
}
