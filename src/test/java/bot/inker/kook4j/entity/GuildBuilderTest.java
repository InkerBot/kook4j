package bot.inker.kook4j.entity;

import bot.inker.kook4j.Kook4jCodec;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GuildBuilderTest {

    @Test
    void builder_setsAllFields() {
        var guild = Guild.builder()
                .id("g-1")
                .name("Test Guild")
                .topic("a topic")
                .userId("owner-id")
                .region("us-west")
                .boostNum(3)
                .level(2)
                .enableOpen(true)
                .build();

        assertEquals("g-1", guild.id());
        assertEquals("Test Guild", guild.name());
        assertEquals("a topic", guild.topic());
        assertEquals("owner-id", guild.userId());
        assertEquals("us-west", guild.region());
        assertEquals(3, guild.boostNum());
        assertEquals(2, guild.level());
        assertTrue(guild.enableOpen());
    }

    @Test
    void toBuilder_copiesNestedLists() {
        var role  = Role.builder().roleId(1).name("admin").build();
        var guild = Guild.builder()
                .id("g-2")
                .name("With Roles")
                .roles(List.of(role))
                .build();

        var copy = guild.toBuilder().name("Renamed").build();
        assertEquals("g-2", copy.id());
        assertEquals("Renamed", copy.name());
        assertEquals(1, copy.roles().size());
        assertEquals(1, copy.roles().getFirst().roleId());
    }

    @Test
    void jsonRoundTrip_withNestedRoles() {
        var role  = Role.builder().roleId(1).name("admin").permissions(1).build();
        var guild = Guild.builder()
                .id("g-rt")
                .name("RT Guild")
                .roles(List.of(role))
                .build();

        var json    = Kook4jCodec.toJson(guild);
        var decoded = Kook4jCodec.fromJson(json, Guild.class);

        assertEquals("g-rt", decoded.id());
        assertEquals("RT Guild", decoded.name());
        assertNotNull(decoded.roles());
        assertEquals(1, decoded.roles().size());
        assertEquals(1, decoded.roles().getFirst().roleId());
    }
}
