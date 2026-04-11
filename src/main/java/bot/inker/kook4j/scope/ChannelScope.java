package bot.inker.kook4j.scope;

import bot.inker.kook4j.Kook4jCodec;
import bot.inker.kook4j.MessageListDirection;
import bot.inker.kook4j.entity.*;
import bot.inker.kook4j.request.ChannelUpdateRequest;
import bot.inker.kook4j.request.MessageCreateRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ChannelScope extends AbstractScope {

    String channelId();

    default CompletableFuture<Channel> viewAsync() {
        return viewAsync(false);
    }

    default CompletableFuture<Channel> viewAsync(boolean needChildren) {
        return http().getAsync("/channel/view", Map.of(
                "target_id", channelId(),
                "need_children", String.valueOf(needChildren)
        )).thenApply(data -> Kook4jCodec.fromJson(data, Channel.class));
    }

    default CompletableFuture<Channel> updateAsync(ChannelUpdateRequest request) {
        var body = Kook4jCodec.toJsonObject(request);
        body.addProperty("channel_id", channelId());
        return http().postAsync("/channel/update", body)
                .thenApply(data -> Kook4jCodec.fromJson(data, Channel.class));
    }

    default CompletableFuture<Void> deleteAsync() {
        var body = new JsonObject();
        body.addProperty("channel_id", channelId());
        return http().postAsync("/channel/delete", body).thenAccept(__ -> {
        });
    }

    default CompletableFuture<String> sendAsync(String content) {
        return sendAsync(MessageCreateRequest.builder().content(content).build());
    }

    default CompletableFuture<String> sendAsync(MessageCreateRequest request) {
        var body = Kook4jCodec.toJsonObject(request);
        body.addProperty("target_id", channelId());
        return http().postAsync("/message/create", body)
                .thenApply(data -> data.getAsJsonObject().get("msg_id").getAsString());
    }

    default CompletableFuture<List<Message>> messagesAsync() {
        return messagesAsync(null, null, 50);
    }

    default CompletableFuture<List<Message>> messagesAsync(String msgId, MessageListDirection direction, int pageSize) {
        var params = new HashMap<String, String>();
        params.put("target_id", channelId());
        params.put("page_size", String.valueOf(pageSize));
        if (msgId != null) params.put("msg_id", msgId);
        if (direction != null) params.put("flag", direction.value());
        return http().getAsync("/message/list", params)
                .thenApply(data -> {
                    var obj = data.getAsJsonObject();
                    return Kook4jCodec.fromJson(obj.get("items"), new TypeToken<List<Message>>() {
                    }.getType());
                });
    }

    default CompletableFuture<List<User>> voiceUsersAsync() {
        return http().getAsync("/channel/user-list", Map.of("channel_id", channelId()))
                .thenApply(data -> Kook4jCodec.fromJson(data, new TypeToken<List<User>>() {
                }.getType()));
    }

    default CompletableFuture<Void> moveUserAsync(String targetChannelId, List<String> userIds) {
        var body = new JsonObject();
        body.addProperty("target_id", targetChannelId);
        var arr = new JsonArray();
        userIds.forEach(arr::add);
        body.add("user_ids", arr);
        return http().postAsync("/channel/move-user", body).thenAccept(__ -> {
        });
    }

    default CompletableFuture<Void> kickVoiceAsync(String userId) {
        var body = new JsonObject();
        body.addProperty("channel_id", channelId());
        body.addProperty("user_id", userId);
        return http().postAsync("/channel/kickout", body).thenAccept(__ -> {
        });
    }

    default CompletableFuture<ChannelRolePermissions> rolePermissionsAsync() {
        return http().getAsync("/channel-role/index", Map.of("channel_id", channelId()))
                .thenApply(data -> Kook4jCodec.fromJson(data, ChannelRolePermissions.class));
    }

    default CompletableFuture<ChannelRoleEntry> createRolePermissionAsync(String type, String value) {
        var body = new JsonObject();
        body.addProperty("channel_id", channelId());
        if (type != null) body.addProperty("type", type);
        if (value != null) body.addProperty("value", value);
        return http().postAsync("/channel-role/create", body)
                .thenApply(data -> Kook4jCodec.fromJson(data, ChannelRoleEntry.class));
    }

    default CompletableFuture<ChannelRoleEntry> updateRolePermissionAsync(String type, String value, int allow, int deny) {
        var body = new JsonObject();
        body.addProperty("channel_id", channelId());
        if (type != null) body.addProperty("type", type);
        if (value != null) body.addProperty("value", value);
        body.addProperty("allow", allow);
        body.addProperty("deny", deny);
        return http().postAsync("/channel-role/update", body)
                .thenApply(data -> Kook4jCodec.fromJson(data, ChannelRoleEntry.class));
    }

    default CompletableFuture<ChannelRolePermissions> syncRolePermissionsAsync() {
        var body = new JsonObject();
        body.addProperty("channel_id", channelId());
        return http().postAsync("/channel-role/sync", body)
                .thenApply(data -> Kook4jCodec.fromJson(data, ChannelRolePermissions.class));
    }

    default CompletableFuture<Void> deleteRolePermissionAsync(String type, String value) {
        var body = new JsonObject();
        body.addProperty("channel_id", channelId());
        if (type != null) body.addProperty("type", type);
        if (value != null) body.addProperty("value", value);
        return http().postAsync("/channel-role/delete", body).thenAccept(__ -> {
        });
    }

    default Channel view() {
        return AbstractScope.sync(viewAsync());
    }

    default Channel view(boolean needChildren) {
        return AbstractScope.sync(viewAsync(needChildren));
    }

    default Channel update(ChannelUpdateRequest request) {
        return AbstractScope.sync(updateAsync(request));
    }

    default void delete() {
        AbstractScope.sync(deleteAsync());
    }

    default String send(String content) {
        return AbstractScope.sync(sendAsync(content));
    }

    default String send(MessageCreateRequest request) {
        return AbstractScope.sync(sendAsync(request));
    }

    default List<Message> messages() {
        return AbstractScope.sync(messagesAsync());
    }

    default List<Message> messages(String msgId, MessageListDirection direction, int pageSize) {
        return AbstractScope.sync(messagesAsync(msgId, direction, pageSize));
    }

    default List<User> voiceUsers() {
        return AbstractScope.sync(voiceUsersAsync());
    }

    default void moveUser(String targetChannelId, List<String> userIds) {
        AbstractScope.sync(moveUserAsync(targetChannelId, userIds));
    }

    default void kickVoice(String userId) {
        AbstractScope.sync(kickVoiceAsync(userId));
    }

    default ChannelRolePermissions rolePermissions() {
        return AbstractScope.sync(rolePermissionsAsync());
    }

    default ChannelRoleEntry createRolePermission(String type, String value) {
        return AbstractScope.sync(createRolePermissionAsync(type, value));
    }

    default ChannelRoleEntry updateRolePermission(String type, String value, int allow, int deny) {
        return AbstractScope.sync(updateRolePermissionAsync(type, value, allow, deny));
    }

    default ChannelRolePermissions syncRolePermissions() {
        return AbstractScope.sync(syncRolePermissionsAsync());
    }

    default void deleteRolePermission(String type, String value) {
        AbstractScope.sync(deleteRolePermissionAsync(type, value));
    }
}
