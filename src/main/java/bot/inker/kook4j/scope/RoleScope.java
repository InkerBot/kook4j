package bot.inker.kook4j.scope;

import bot.inker.kook4j.Kook4jCodec;
import bot.inker.kook4j.entity.Role;
import bot.inker.kook4j.http.HttpClient;
import bot.inker.kook4j.request.RoleUpdateRequest;
import com.google.gson.JsonObject;

import java.util.concurrent.CompletableFuture;

public record RoleScope(HttpClient http, String guildId, int roleId) implements AbstractScope {

    public CompletableFuture<Role> updateAsync(RoleUpdateRequest request) {
        var body = Kook4jCodec.toJsonObject(request);
        body.addProperty("guild_id", guildId);
        body.addProperty("role_id", roleId);
        return http.postAsync("/guild-role/update", body)
                .thenApply(data -> Kook4jCodec.fromJson(data, Role.class));
    }

    public CompletableFuture<Void> deleteAsync() {
        var body = new JsonObject();
        body.addProperty("guild_id", guildId);
        body.addProperty("role_id", roleId);
        return http.postAsync("/guild-role/delete", body).thenAccept(__ -> {
        });
    }

    public CompletableFuture<Void> grantAsync(String userId) {
        var body = new JsonObject();
        body.addProperty("guild_id", guildId);
        body.addProperty("user_id", userId);
        body.addProperty("role_id", roleId);
        return http.postAsync("/guild-role/grant", body).thenAccept(__ -> {
        });
    }

    public CompletableFuture<Void> revokeAsync(String userId) {
        var body = new JsonObject();
        body.addProperty("guild_id", guildId);
        body.addProperty("user_id", userId);
        body.addProperty("role_id", roleId);
        return http.postAsync("/guild-role/revoke", body).thenAccept(__ -> {
        });
    }

    public Role update(RoleUpdateRequest request) {
        return AbstractScope.sync(updateAsync(request));
    }

    public void delete() {
        AbstractScope.sync(deleteAsync());
    }

    public void grant(String userId) {
        AbstractScope.sync(grantAsync(userId));
    }

    public void revoke(String userId) {
        AbstractScope.sync(revokeAsync(userId));
    }
}
