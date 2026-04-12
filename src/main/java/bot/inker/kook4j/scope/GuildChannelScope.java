package bot.inker.kook4j.scope;

import bot.inker.kook4j.ChannelType;
import bot.inker.kook4j.Kook4jCodec;
import bot.inker.kook4j.entity.Channel;
import bot.inker.kook4j.entity.PagedList;
import bot.inker.kook4j.request.ChannelCreateRequest;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GuildChannelScope extends AbstractScope {

    String guildId();

    default CompletableFuture<PagedList<Channel>> listChannelsAsync() {
        return listChannelsAsync(null);
    }

    default CompletableFuture<PagedList<Channel>> listChannelsAsync(ChannelType type) {
        return listChannelsAsync(type, 1, 50);
    }

    default CompletableFuture<PagedList<Channel>> listChannelsAsync(ChannelType type, int page, int pageSize) {
        var params = new HashMap<String, String>();
        params.put("guild_id", guildId());
        params.put("page", String.valueOf(page));
        params.put("page_size", String.valueOf(pageSize));
        if (type != null) params.put("type", String.valueOf(type.value()));
        return http().getAsync("/channel/list", params).thenApply(data -> {
            var obj = data.getAsJsonObject();
            List<Channel> items = http().decode(obj.get("items"), new TypeToken<>() {
            });
            var meta = Kook4jCodec.fromJson(obj.get("meta"), PagedList.Meta.class);
            return new PagedList<>(items, meta, (p, ps) -> listChannelsAsync(type, p, ps));
        });
    }

    default CompletableFuture<Channel> createChannelAsync(ChannelCreateRequest request) {
        var body = Kook4jCodec.toJsonObject(request);
        body.addProperty("guild_id", guildId());
        return http().postAsync("/channel/create", body)
                .thenApply(data -> http().decode(data, Channel.class));
    }

    default PagedList<Channel> listChannels() {
        return AbstractScope.sync(listChannelsAsync());
    }

    default PagedList<Channel> listChannels(ChannelType type) {
        return AbstractScope.sync(listChannelsAsync(type));
    }

    default Channel createChannel(ChannelCreateRequest request) {
        return AbstractScope.sync(createChannelAsync(request));
    }
}
