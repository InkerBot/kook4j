package bot.inker.kook4j.scope;

import bot.inker.kook4j.Kook4jCodec;
import bot.inker.kook4j.entity.User;
import bot.inker.kook4j.entity.UserChat;
import bot.inker.kook4j.request.IntimacyUpdateRequest;
import bot.inker.kook4j.request.MessageCreateRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface UserScope extends AbstractScope {

    String userId();

    default CompletableFuture<User> viewAsync() {
        return viewAsync(null);
    }

    default CompletableFuture<User> viewAsync(String guildId) {
        var params = new HashMap<String, String>();
        params.put("user_id", userId());
        if (guildId != null) params.put("guild_id", guildId);
        return http().getAsync("/user/view", params)
                .thenApply(data -> http().decode(data, User.class));
    }

    default CompletableFuture<JsonElement> intimacyAsync() {
        return http().getAsync("/intimacy/index", Map.of("user_id", userId()));
    }

    default CompletableFuture<Void> updateIntimacyAsync(IntimacyUpdateRequest request) {
        var body = Kook4jCodec.toJsonObject(request);
        body.addProperty("user_id", userId());
        return http().postAsync("/intimacy/update", body).thenAccept(__ -> {
        });
    }

    default CompletableFuture<UserChat> createChatAsync() {
        var body = new JsonObject();
        body.addProperty("target_id", userId());
        return http().postAsync("/user-chat/create", body)
                .thenApply(data -> http().decode(data, UserChat.class));
    }

    default CompletableFuture<String> sendDmAsync(String content) {
        return sendDmAsync(MessageCreateRequest.builder().content(content).build());
    }

    default CompletableFuture<String> sendDmAsync(MessageCreateRequest request) {
        var body = Kook4jCodec.toJsonObject(request);
        body.addProperty("target_id", userId());
        return http().postAsync("/direct-message/create", body)
                .thenApply(data -> data.getAsJsonObject().get("msg_id").getAsString());
    }

    default User view() {
        return AbstractScope.sync(viewAsync());
    }

    default User view(String guildId) {
        return AbstractScope.sync(viewAsync(guildId));
    }

    default JsonElement intimacy() {
        return AbstractScope.sync(intimacyAsync());
    }

    default void updateIntimacy(IntimacyUpdateRequest request) {
        AbstractScope.sync(updateIntimacyAsync(request));
    }

    default UserChat createChat() {
        return AbstractScope.sync(createChatAsync());
    }

    default String sendDm(String content) {
        return AbstractScope.sync(sendDmAsync(content));
    }

    default String sendDm(MessageCreateRequest request) {
        return AbstractScope.sync(sendDmAsync(request));
    }
}
