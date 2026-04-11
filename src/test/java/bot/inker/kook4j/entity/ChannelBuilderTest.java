package bot.inker.kook4j.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChannelBuilderTest {

    @Test
    void builder_setsFields() {
        var ch = Channel.builder()
                .id("ch-1")
                .name("general")
                .type(1)
                .guildId("g-1")
                .isCategory(false)
                .build();

        assertEquals("ch-1", ch.id());
        assertEquals("general", ch.name());
        assertEquals(1, ch.type());
        assertEquals("g-1", ch.guildId());
        assertFalse(ch.isCategory());
    }

    @Test
    void isText_returnsTrueForType1() {
        var ch = Channel.builder().type(1).build();
        assertTrue(ch.isText());
        assertFalse(ch.isVoice());
    }

    @Test
    void isVoice_returnsTrueForType2() {
        var ch = Channel.builder().type(2).build();
        assertTrue(ch.isVoice());
        assertFalse(ch.isText());
    }

    @Test
    void toBuilder_preservesId() {
        var original = Channel.builder().id("ch-2").name("old-name").build();
        var copy = original.toBuilder().name("new-name").build();
        assertEquals("ch-2", copy.id());
        assertEquals("new-name", copy.name());
        assertEquals("old-name", original.name());
    }
}
