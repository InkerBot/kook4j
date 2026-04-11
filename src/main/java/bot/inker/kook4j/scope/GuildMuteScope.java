package bot.inker.kook4j.scope;

import bot.inker.kook4j.Kook4jCodec;
import bot.inker.kook4j.MuteType;
import bot.inker.kook4j.entity.GuildMuteList;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface GuildMuteScope extends AbstractScope {

    String guildId();

    default CompletableFuture<Void> muteAsync(String userId, MuteType type) {
        var body = new JsonObject();
        body.addProperty("guild_id", guildId());
        body.addProperty("user_id", userId);
        body.addProperty("type", type.value());
        return http().postAsync("/guild-mute/create", body).thenAccept(__ -> {
        });
    }

    default CompletableFuture<Void> unmuteAsync(String userId, MuteType type) {
        var body = new JsonObject();
        body.addProperty("guild_id", guildId());
        body.addProperty("user_id", userId);
        body.addProperty("type", type.value());
        return http().postAsync("/guild-mute/delete", body).thenAccept(__ -> {
        });
    }

    default CompletableFuture<GuildMuteList> muteListAsync() {
        return http().getAsync("/guild-mute/list", Map.of("guild_id", guildId(), "return_type", "detail"))
                .thenApply(data -> Kook4jCodec.fromJson(data, GuildMuteList.class));
    }

    default void mute(String userId, MuteType type) {
        AbstractScope.sync(muteAsync(userId, type));
    }

    default void unmute(String userId, MuteType type) {
        AbstractScope.sync(unmuteAsync(userId, type));
    }

    default GuildMuteList muteList() {
        return AbstractScope.sync(muteListAsync());
    }
}
