package bot.inker.kook4j.scope;

import bot.inker.kook4j.Kook4jCodec;
import bot.inker.kook4j.entity.Channel;
import bot.inker.kook4j.entity.PagedList;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface GuildChannelUserScope extends AbstractScope {

    String guildId();

    default CompletableFuture<PagedList<Channel>> getJoinedChannelAsync(String userId) {
        return getJoinedChannelAsync(userId, 1, 50);
    }

    default CompletableFuture<PagedList<Channel>> getJoinedChannelAsync(String userId, int page, int pageSize) {
        return http().getAsync("/channel-user/get-joined-channel", Map.of(
                        "guild_id", guildId(), "user_id", userId,
                        "page", String.valueOf(page), "page_size", String.valueOf(pageSize)))
                .thenApply(data -> {
                    var obj = data.getAsJsonObject();
                    List<Channel> items = http().decode(obj.get("items"), new TypeToken<>() {
                    });
                    var meta = Kook4jCodec.fromJson(obj.get("meta"), PagedList.Meta.class);
                    return new PagedList<>(items, meta, (p, ps) -> getJoinedChannelAsync(userId, p, ps));
                });
    }

    default PagedList<Channel> getJoinedChannel(String userId) {
        return AbstractScope.sync(getJoinedChannelAsync(userId));
    }
}
