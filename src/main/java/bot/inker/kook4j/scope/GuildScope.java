package bot.inker.kook4j.scope;

import bot.inker.kook4j.Kook4jCodec;
import bot.inker.kook4j.entity.Guild;
import bot.inker.kook4j.entity.PagedList;
import bot.inker.kook4j.entity.User;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface GuildScope extends GuildChannelScope, GuildRoleScope, GuildChannelUserScope,
        GuildEmojiScope, GuildInviteScope, GuildMuteScope, GuildBoostScope, GuildBadgeScope {

    String guildId();

    default CompletableFuture<Guild> viewAsync() {
        return http().getAsync("/guild/view", Map.of("guild_id", guildId()))
                .thenApply(data -> Kook4jCodec.fromJson(data, Guild.class));
    }

    default CompletableFuture<PagedList<User>> membersAsync() {
        return membersAsync(1, 50);
    }

    default CompletableFuture<PagedList<User>> membersAsync(int page, int pageSize) {
        return http().getAsync("/guild/user-list", Map.of(
                "guild_id", guildId(),
                "page", String.valueOf(page),
                "page_size", String.valueOf(pageSize)
        )).thenApply(data -> {
            var obj = data.getAsJsonObject();
            List<User> items = Kook4jCodec.fromJson(obj.get("items"), new TypeToken<List<User>>() {
            }.getType());
            var meta = Kook4jCodec.fromJson(obj.get("meta"), PagedList.Meta.class);
            return new PagedList<>(items, meta, this::membersAsync);
        });
    }

    default CompletableFuture<Void> nicknameAsync(String nickname) {
        return nicknameAsync(null, nickname);
    }

    default CompletableFuture<Void> nicknameAsync(String userId, String nickname) {
        var body = new JsonObject();
        body.addProperty("guild_id", guildId());
        body.addProperty("nickname", nickname);
        if (userId != null) body.addProperty("user_id", userId);
        return http().postAsync("/guild/nickname", body).thenAccept(__ -> {
        });
    }

    default CompletableFuture<Void> leaveAsync() {
        var body = new JsonObject();
        body.addProperty("guild_id", guildId());
        return http().postAsync("/guild/leave", body).thenAccept(__ -> {
        });
    }

    default CompletableFuture<Void> kickAsync(String targetId) {
        var body = new JsonObject();
        body.addProperty("guild_id", guildId());
        body.addProperty("target_id", targetId);
        return http().postAsync("/guild/kickout", body).thenAccept(__ -> {
        });
    }

    default Guild view() {
        return AbstractScope.sync(viewAsync());
    }

    default PagedList<User> members() {
        return AbstractScope.sync(membersAsync());
    }

    default PagedList<User> members(int page, int pageSize) {
        return AbstractScope.sync(membersAsync(page, pageSize));
    }

    default void nickname(String nickname) {
        AbstractScope.sync(nicknameAsync(nickname));
    }

    default void nickname(String userId, String nickname) {
        AbstractScope.sync(nicknameAsync(userId, nickname));
    }

    default void leave() {
        AbstractScope.sync(leaveAsync());
    }

    default void kick(String targetId) {
        AbstractScope.sync(kickAsync(targetId));
    }
}
