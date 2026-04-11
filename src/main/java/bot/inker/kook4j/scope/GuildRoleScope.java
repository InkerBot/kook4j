package bot.inker.kook4j.scope;

import bot.inker.kook4j.Kook4jCodec;
import bot.inker.kook4j.entity.PagedList;
import bot.inker.kook4j.entity.Role;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface GuildRoleScope extends AbstractScope {

    String guildId();

    default CompletableFuture<PagedList<Role>> listRolesAsync() {
        return listRolesAsync(1, 50);
    }

    default CompletableFuture<PagedList<Role>> listRolesAsync(int page, int pageSize) {
        return http().getAsync("/guild-role/list", Map.of(
                "guild_id", guildId(),
                "page", String.valueOf(page),
                "page_size", String.valueOf(pageSize)
        )).thenApply(data -> {
            var obj = data.getAsJsonObject();
            List<Role> items = Kook4jCodec.fromJson(obj.get("items"), new TypeToken<List<Role>>() {
            }.getType());
            var meta = Kook4jCodec.fromJson(obj.get("meta"), PagedList.Meta.class);
            return new PagedList<>(items, meta, this::listRolesAsync);
        });
    }

    default CompletableFuture<Role> createRoleAsync(String name) {
        var body = new JsonObject();
        body.addProperty("guild_id", guildId());
        body.addProperty("name", name);
        return http().postAsync("/guild-role/create", body)
                .thenApply(data -> Kook4jCodec.fromJson(data, Role.class));
    }

    default RoleScope role(int roleId) {
        return new RoleScope(http(), guildId(), roleId);
    }

    default PagedList<Role> listRoles() {
        return AbstractScope.sync(listRolesAsync());
    }

    default PagedList<Role> listRoles(int page, int pageSize) {
        return AbstractScope.sync(listRolesAsync(page, pageSize));
    }

    default Role createRole(String name) {
        return AbstractScope.sync(createRoleAsync(name));
    }
}
