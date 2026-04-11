package bot.inker.kook4j.scope;

import bot.inker.kook4j.Kook4jCodec;
import bot.inker.kook4j.http.HttpClient;
import bot.inker.kook4j.request.MessageCreateRequest;

import java.util.concurrent.CompletableFuture;

public record DmScope(HttpClient http, String targetId) implements AbstractScope {

    public CompletableFuture<String> sendAsync(String content) {
        return sendAsync(MessageCreateRequest.builder().content(content).build());
    }

    public CompletableFuture<String> sendAsync(MessageCreateRequest request) {
        var body = Kook4jCodec.toJsonObject(request);
        body.addProperty("target_id", targetId);
        return http.postAsync("/direct-message/create", body)
                .thenApply(data -> data.getAsJsonObject().get("msg_id").getAsString());
    }

    public String send(String content) {
        return AbstractScope.sync(sendAsync(content));
    }

    public String send(MessageCreateRequest request) {
        return AbstractScope.sync(sendAsync(request));
    }
}
