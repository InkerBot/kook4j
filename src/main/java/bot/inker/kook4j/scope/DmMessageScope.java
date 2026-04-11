package bot.inker.kook4j.scope;

import bot.inker.kook4j.Kook4jCodec;
import bot.inker.kook4j.entity.Message;
import bot.inker.kook4j.entity.User;
import bot.inker.kook4j.http.HttpClient;
import bot.inker.kook4j.request.MessageUpdateRequest;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public record DmMessageScope(HttpClient http, String chatCode, String msgId) implements AbstractScope {

    public CompletableFuture<Message> viewAsync() {
        return http.getAsync("/direct-message/view", Map.of(
                        "chat_code", chatCode, "msg_id", msgId))
                .thenApply(data -> Kook4jCodec.fromJson(data, Message.class));
    }

    public CompletableFuture<Void> updateAsync(String content) {
        return updateAsync(MessageUpdateRequest.builder().content(content).build());
    }

    public CompletableFuture<Void> updateAsync(MessageUpdateRequest request) {
        var body = Kook4jCodec.toJsonObject(request);
        body.addProperty("msg_id", msgId);
        return http.postAsync("/direct-message/update", body).thenAccept(__ -> {
        });
    }

    public CompletableFuture<Void> deleteAsync() {
        var body = new JsonObject();
        body.addProperty("msg_id", msgId);
        return http.postAsync("/direct-message/delete", body).thenAccept(__ -> {
        });
    }

    public CompletableFuture<Void> addReactionAsync(String emoji) {
        var body = new JsonObject();
        body.addProperty("msg_id", msgId);
        body.addProperty("emoji", emoji);
        return http.postAsync("/direct-message/add-reaction", body).thenAccept(__ -> {
        });
    }

    public CompletableFuture<Void> removeReactionAsync(String emoji) {
        return removeReactionAsync(emoji, null);
    }

    public CompletableFuture<Void> removeReactionAsync(String emoji, String userId) {
        var body = new JsonObject();
        body.addProperty("msg_id", msgId);
        body.addProperty("emoji", emoji);
        if (userId != null) body.addProperty("user_id", userId);
        return http.postAsync("/direct-message/delete-reaction", body).thenAccept(__ -> {
        });
    }

    public CompletableFuture<List<User>> reactionsAsync(String emoji) {
        return http.getAsync("/direct-message/reaction-list", Map.of(
                        "msg_id", msgId, "emoji", emoji))
                .thenApply(data -> Kook4jCodec.fromJson(data, new TypeToken<List<User>>() {
                }.getType()));
    }

    public Message view() {
        return AbstractScope.sync(viewAsync());
    }

    public void update(String content) {
        AbstractScope.sync(updateAsync(content));
    }

    public void update(MessageUpdateRequest request) {
        AbstractScope.sync(updateAsync(request));
    }

    public void delete() {
        AbstractScope.sync(deleteAsync());
    }

    public void addReaction(String emoji) {
        AbstractScope.sync(addReactionAsync(emoji));
    }

    public void removeReaction(String emoji) {
        AbstractScope.sync(removeReactionAsync(emoji));
    }

    public void removeReaction(String emoji, String userId) {
        AbstractScope.sync(removeReactionAsync(emoji, userId));
    }

    public List<User> reactions(String emoji) {
        return AbstractScope.sync(reactionsAsync(emoji));
    }
}
