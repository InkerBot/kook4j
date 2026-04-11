package bot.inker.kook4j.scope;

import bot.inker.kook4j.Kook4jCodec;
import bot.inker.kook4j.entity.GuildBoostRecord;
import bot.inker.kook4j.entity.PagedList;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GuildBoostScope extends AbstractScope {

    String guildId();

    default CompletableFuture<PagedList<GuildBoostRecord>> boostHistoryAsync() {
        return boostHistoryAsync(null, null);
    }

    default CompletableFuture<PagedList<GuildBoostRecord>> boostHistoryAsync(Long startTime, Long endTime) {
        return boostHistoryAsync(startTime, endTime, 1, 50);
    }

    default CompletableFuture<PagedList<GuildBoostRecord>> boostHistoryAsync(Long startTime, Long endTime, int page, int pageSize) {
        var params = new HashMap<String, String>();
        params.put("guild_id", guildId());
        if (startTime != null) params.put("start_time", String.valueOf(startTime));
        if (endTime != null) params.put("end_time", String.valueOf(endTime));
        params.put("page", String.valueOf(page));
        params.put("page_size", String.valueOf(pageSize));
        return http().getAsync("/guild-boost/history", params).thenApply(data -> {
            var obj = data.getAsJsonObject();
            List<GuildBoostRecord> items = Kook4jCodec.fromJson(obj.get("items"), new TypeToken<List<GuildBoostRecord>>() {
            }.getType());
            var meta = Kook4jCodec.fromJson(obj.get("meta"), PagedList.Meta.class);
            return new PagedList<>(items, meta, (p, ps) -> boostHistoryAsync(startTime, endTime, p, ps));
        });
    }

    default PagedList<GuildBoostRecord> boostHistory() {
        return AbstractScope.sync(boostHistoryAsync());
    }

    default PagedList<GuildBoostRecord> boostHistory(Long startTime, Long endTime) {
        return AbstractScope.sync(boostHistoryAsync(startTime, endTime));
    }
}
