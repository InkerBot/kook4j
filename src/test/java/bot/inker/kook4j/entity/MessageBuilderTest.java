package bot.inker.kook4j.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBuilderTest {

    @Test
    void builder_setsAllFields() {
        var author = User.builder().id("u-1").username("Alice").build();
        var msg = Message.builder()
                .id("msg-1")
                .type(9)
                .content("Hello **world**")
                .author(author)
                .mentionAll(false)
                .createAt(1_700_000_000_000L)
                .build();

        assertEquals("msg-1", msg.id());
        assertEquals(9, msg.type());
        assertEquals("Hello **world**", msg.content());
        assertNotNull(msg.author());
        assertEquals("u-1", msg.author().id());
        assertFalse(msg.mentionAll());
        assertEquals(1_700_000_000_000L, msg.createAt());
    }

    @Test
    void toBuilder_copiesContent() {
        var original = Message.builder()
                .id("msg-2")
                .content("original text")
                .build();

        var updated = original.toBuilder().content("updated text").build();
        assertEquals("msg-2", updated.id());
        assertEquals("updated text", updated.content());
        assertEquals("original text", original.content());
    }
}
