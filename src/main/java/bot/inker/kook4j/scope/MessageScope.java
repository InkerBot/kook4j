package bot.inker.kook4j.scope;

import bot.inker.kook4j.Kook4jCodec;
import bot.inker.kook4j.entity.Message;
import bot.inker.kook4j.entity.User;
import bot.inker.kook4j.request.MessageUpdateRequest;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface MessageScope extends AbstractScope {

    String msgId();

    default CompletableFuture<Message> viewAsync() {
        return http().getAsync("/message/view", Map.of("msg_id", msgId()))
                .thenApply(data -> http().decode(data, Message.class));
    }

    default CompletableFuture<Void> updateAsync(String content) {
        return updateAsync(MessageUpdateRequest.builder().content(content).build());
    }

    default CompletableFuture<Void> updateAsync(MessageUpdateRequest request) {
        var body = Kook4jCodec.toJsonObject(request);
        body.addProperty("msg_id", msgId());
        return http().postAsync("/message/update", body).thenAccept(__ -> {
        });
    }

    default CompletableFuture<Void> deleteAsync() {
        var body = new JsonObject();
        body.addProperty("msg_id", msgId());
        return http().postAsync("/message/delete", body).thenAccept(__ -> {
        });
    }

    default CompletableFuture<Void> addReactionAsync(String emoji) {
        var body = new JsonObject();
        body.addProperty("msg_id", msgId());
        body.addProperty("emoji", emoji);
        return http().postAsync("/message/add-reaction", body).thenAccept(__ -> {
        });
    }

    default CompletableFuture<Void> removeReactionAsync(String emoji) {
        return removeReactionAsync(emoji, null);
    }

    default CompletableFuture<Void> removeReactionAsync(String emoji, String userId) {
        var body = new JsonObject();
        body.addProperty("msg_id", msgId());
        body.addProperty("emoji", emoji);
        if (userId != null) body.addProperty("user_id", userId);
        return http().postAsync("/message/delete-reaction", body).thenAccept(__ -> {
        });
    }

    default CompletableFuture<List<User>> reactionUsersAsync(String emoji) {
        return http().getAsync("/message/reaction-list", Map.of(
                        "msg_id", msgId(), "emoji", emoji))
                .thenApply(data -> http().<List<User>>decode(data, new TypeToken<>() {
                }));
    }

    default CompletableFuture<Void> pinAsync(String channelId) {
        var body = new JsonObject();
        body.addProperty("msg_id", msgId());
        body.addProperty("target_id", channelId);
        return http().postAsync("/message/pin", body).thenAccept(__ -> {
        });
    }

    default CompletableFuture<Void> unpinAsync(String channelId) {
        var body = new JsonObject();
        body.addProperty("msg_id", msgId());
        body.addProperty("target_id", channelId);
        return http().postAsync("/message/unpin", body).thenAccept(__ -> {
        });
    }

    default CompletableFuture<Void> sendPipeMessageAsync(String accessToken, JsonObject body) {
        return sendPipeMessageAsync(accessToken, null, null, body);
    }

    default CompletableFuture<Void> sendPipeMessageAsync(String accessToken, Integer type, String targetId, JsonObject body) {
        var params = new HashMap<String, String>();
        params.put("access_token", accessToken);
        if (type != null) params.put("type", String.valueOf(type));
        if (targetId != null) params.put("target_id", targetId);
        return http().postWithQueryAsync("/message/send-pipemsg", params, body).thenAccept(__ -> {
        });
    }

    default Message view() {
        return AbstractScope.sync(viewAsync());
    }

    default void update(String content) {
        AbstractScope.sync(updateAsync(content));
    }

    default void update(MessageUpdateRequest request) {
        AbstractScope.sync(updateAsync(request));
    }

    default void delete() {
        AbstractScope.sync(deleteAsync());
    }

    default void addReaction(String emoji) {
        AbstractScope.sync(addReactionAsync(emoji));
    }

    default void removeReaction(String emoji) {
        AbstractScope.sync(removeReactionAsync(emoji));
    }

    default void removeReaction(String emoji, String userId) {
        AbstractScope.sync(removeReactionAsync(emoji, userId));
    }

    default List<User> reactionUsers(String emoji) {
        return AbstractScope.sync(reactionUsersAsync(emoji));
    }

    default void pin(String channelId) {
        AbstractScope.sync(pinAsync(channelId));
    }

    default void unpin(String channelId) {
        AbstractScope.sync(unpinAsync(channelId));
    }

    default void sendPipeMessage(String accessToken, JsonObject body) {
        AbstractScope.sync(sendPipeMessageAsync(accessToken, body));
    }

    default void sendPipeMessage(String accessToken, Integer type, String targetId, JsonObject body) {
        AbstractScope.sync(sendPipeMessageAsync(accessToken, type, targetId, body));
    }
}
