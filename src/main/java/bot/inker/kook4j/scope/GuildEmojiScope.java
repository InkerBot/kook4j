package bot.inker.kook4j.scope;

import bot.inker.kook4j.Kook4jCodec;
import bot.inker.kook4j.entity.GuildEmoji;
import bot.inker.kook4j.entity.PagedList;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface GuildEmojiScope extends AbstractScope {

    String guildId();

    default CompletableFuture<PagedList<GuildEmoji>> listEmojisAsync() {
        return listEmojisAsync(1, 50);
    }

    default CompletableFuture<PagedList<GuildEmoji>> listEmojisAsync(int page, int pageSize) {
        return http().getAsync("/guild-emoji/list", Map.of(
                "guild_id", guildId(),
                "page", String.valueOf(page),
                "page_size", String.valueOf(pageSize)
        )).thenApply(data -> {
            var obj = data.getAsJsonObject();
            List<GuildEmoji> items = Kook4jCodec.fromJson(obj.get("items"), new TypeToken<List<GuildEmoji>>() {
            }.getType());
            var meta = Kook4jCodec.fromJson(obj.get("meta"), PagedList.Meta.class);
            return new PagedList<>(items, meta, this::listEmojisAsync);
        });
    }

    default CompletableFuture<GuildEmoji> createEmojiAsync(String name, File emojiFile) {
        return http().postFormAsync("/guild-emoji/create",
                        Map.of("guild_id", guildId(), "name", name), "emoji", emojiFile)
                .thenApply(data -> Kook4jCodec.fromJson(data, GuildEmoji.class));
    }

    default CompletableFuture<Void> updateEmojiAsync(String id, String name) {
        var body = new JsonObject();
        body.addProperty("id", id);
        body.addProperty("name", name);
        return http().postAsync("/guild-emoji/update", body).thenAccept(__ -> {
        });
    }

    default CompletableFuture<Void> deleteEmojiAsync(String id) {
        var body = new JsonObject();
        body.addProperty("id", id);
        return http().postAsync("/guild-emoji/delete", body).thenAccept(__ -> {
        });
    }

    default PagedList<GuildEmoji> listEmojis() {
        return AbstractScope.sync(listEmojisAsync());
    }

    default GuildEmoji createEmoji(String name, File emojiFile) {
        return AbstractScope.sync(createEmojiAsync(name, emojiFile));
    }

    default void updateEmoji(String id, String name) {
        AbstractScope.sync(updateEmojiAsync(id, name));
    }

    default void deleteEmoji(String id) {
        AbstractScope.sync(deleteEmojiAsync(id));
    }
}
