package bot.inker.kook4j.entity;

import bot.inker.kook4j.Kook4jCodec;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserBuilderTest {

    @Test
    void builder_setsAllFields() {
        var user = User.builder()
                .id("u-1")
                .username("Alice")
                .identifyNum("0001")
                .nickname("ali")
                .online(true)
                .bot(false)
                .avatar("https://example.com/avatar.png")
                .status(0)
                .build();

        assertEquals("u-1", user.id());
        assertEquals("Alice", user.username());
        assertEquals("0001", user.identifyNum());
        assertEquals("ali", user.nickname());
        assertTrue(user.online());
        assertFalse(user.bot());
        assertEquals("https://example.com/avatar.png", user.avatar());
        assertEquals(0, user.status());
    }

    @Test
    void toBuilder_preservesAllFields() {
        var original = User.builder()
                .id("u-2")
                .username("Bob")
                .identifyNum("0002")
                .online(false)
                .build();

        var modified = original.toBuilder()
                .username("Bobby")
                .online(true)
                .build();

        // Original unchanged
        assertEquals("Bob", original.username());
        assertFalse(original.online());

        // Modified has new values
        assertEquals("u-2", modified.id());           // copied
        assertEquals("Bobby", modified.username());    // overridden
        assertTrue(modified.online());                 // overridden
    }

    @Test
    void defaultFields_areNullOrFalse() {
        var user = User.builder().id("u-3").build();
        assertNull(user.username());
        assertFalse(user.online());
        assertFalse(user.bot());
        assertNull(user.avatar());
    }

    @Test
    void jsonRoundTrip_preservesFields() {
        var original = User.builder()
                .id("u-rt")
                .username("RoundTrip")
                .identifyNum("9999")
                .online(true)
                .bot(true)
                .status(0)
                .build();

        var json    = Kook4jCodec.toJson(original);
        var decoded = Kook4jCodec.fromJson(json, User.class);

        assertEquals(original.id(), decoded.id());
        assertEquals(original.username(), decoded.username());
        assertEquals(original.identifyNum(), decoded.identifyNum());
        assertEquals(original.online(), decoded.online());
        assertEquals(original.bot(), decoded.bot());
    }
}