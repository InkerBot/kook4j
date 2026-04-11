package bot.inker.kook4j.scope;

import bot.inker.kook4j.Kook4jCodec;
import bot.inker.kook4j.entity.Invite;
import bot.inker.kook4j.entity.PagedList;
import bot.inker.kook4j.request.InviteCreateRequest;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface GuildInviteScope extends AbstractScope {

    String guildId();

    default CompletableFuture<PagedList<Invite>> listInvitesAsync() {
        return listInvitesAsync(1, 50);
    }

    default CompletableFuture<PagedList<Invite>> listInvitesAsync(int page, int pageSize) {
        return http().getAsync("/invite/list", Map.of(
                "guild_id", guildId(),
                "page", String.valueOf(page),
                "page_size", String.valueOf(pageSize)
        )).thenApply(data -> {
            var obj = data.getAsJsonObject();
            List<Invite> items = Kook4jCodec.fromJson(obj.get("items"), new TypeToken<List<Invite>>() {
            }.getType());
            var meta = Kook4jCodec.fromJson(obj.get("meta"), PagedList.Meta.class);
            return new PagedList<>(items, meta, this::listInvitesAsync);
        });
    }

    default CompletableFuture<Invite> createInviteAsync() {
        var body = new JsonObject();
        body.addProperty("guild_id", guildId());
        return http().postAsync("/invite/create", body)
                .thenApply(data -> Kook4jCodec.fromJson(data, Invite.class));
    }

    default CompletableFuture<Invite> createInviteAsync(InviteCreateRequest request) {
        var body = Kook4jCodec.toJsonObject(request);
        body.addProperty("guild_id", guildId());
        return http().postAsync("/invite/create", body)
                .thenApply(data -> Kook4jCodec.fromJson(data, Invite.class));
    }

    default CompletableFuture<Void> deleteInviteAsync(String urlCode) {
        var body = new JsonObject();
        body.addProperty("url_code", urlCode);
        body.addProperty("guild_id", guildId());
        return http().postAsync("/invite/delete", body).thenAccept(__ -> {
        });
    }

    default PagedList<Invite> listInvites() {
        return AbstractScope.sync(listInvitesAsync());
    }

    default Invite createInvite() {
        return AbstractScope.sync(createInviteAsync());
    }

    default Invite createInvite(InviteCreateRequest request) {
        return AbstractScope.sync(createInviteAsync(request));
    }

    default void deleteInvite(String urlCode) {
        AbstractScope.sync(deleteInviteAsync(urlCode));
    }
}
