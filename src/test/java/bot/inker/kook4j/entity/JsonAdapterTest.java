package bot.inker.kook4j.entity;

import bot.inker.kook4j.Kook4jCodec;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonAdapterTest {

    @Test
    void leniently_parsesBooleanFromNumber() {
        var json = "{\"id\":\"u-bool\",\"username\":\"x\",\"online\":1,\"bot\":0}";
        var user = Kook4jCodec.fromJson(json, User.class);
        assertTrue(user.online());
        assertFalse(user.bot());
    }

    @Test
    void leniently_parsesBooleanFromString() {
        var json = "{\"id\":\"u-bool2\",\"username\":\"x\",\"online\":\"true\",\"bot\":\"false\"}";
        var user = Kook4jCodec.fromJson(json, User.class);
        assertTrue(user.online());
        assertFalse(user.bot());
    }

    @Test
    void leniently_parsesBooleanFromNull() {
        var json = "{\"id\":\"u-null\",\"username\":\"x\",\"online\":null}";
        var user = Kook4jCodec.fromJson(json, User.class);
        assertFalse(user.online());   // null -> false per lenient adapter
    }
}
