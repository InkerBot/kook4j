package bot.inker.kook4j.scope;

import bot.inker.kook4j.Kook4jCodec;
import bot.inker.kook4j.entity.Message;
import bot.inker.kook4j.entity.UserChat;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface UserChatScope extends AbstractScope {

    String chatCode();

    default CompletableFuture<UserChat> viewAsync() {
        return http().getAsync("/user-chat/view", Map.of("chat_code", chatCode()))
                .thenApply(data -> http().decode(data, UserChat.class));
    }

    default CompletableFuture<Void> deleteAsync() {
        var body = new JsonObject();
        body.addProperty("chat_code", chatCode());
        return http().postAsync("/user-chat/delete", body).thenAccept(__ -> {
        });
    }

    default CompletableFuture<List<Message>> messagesAsync() {
        return messagesAsync(null, null, 50);
    }

    default CompletableFuture<List<Message>> messagesAsync(String msgId, String flag, int pageSize) {
        var params = new HashMap<String, String>();
        params.put("chat_code", chatCode());
        params.put("page_size", String.valueOf(pageSize));
        if (msgId != null) params.put("msg_id", msgId);
        if (flag != null) params.put("flag", flag);
        return http().getAsync("/direct-message/list", params)
                .thenApply(data -> {
                    var obj = data.getAsJsonObject();
                    return http().<List<Message>>decode(obj.get("items"), new TypeToken<>() {
                    });
                });
    }

    default DmMessageScope message(String msgId) {
        return new DmMessageScope(http(), chatCode(), msgId);
    }

    default UserChat view() {
        return AbstractScope.sync(viewAsync());
    }

    default void delete() {
        AbstractScope.sync(deleteAsync());
    }

    default List<Message> messages() {
        return AbstractScope.sync(messagesAsync());
    }

    default List<Message> messages(String msgId, String flag, int pageSize) {
        return AbstractScope.sync(messagesAsync(msgId, flag, pageSize));
    }
}
