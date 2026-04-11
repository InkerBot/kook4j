package bot.inker.kook4j.scope;

import bot.inker.kook4j.Kook4jCodec;
import bot.inker.kook4j.entity.VoiceConnection;
import bot.inker.kook4j.request.VoiceJoinRequest;
import com.google.gson.JsonObject;

import java.util.concurrent.CompletableFuture;

public interface VoiceScope extends AbstractScope {

    String channelId();

    default CompletableFuture<VoiceConnection> joinAsync() {
        return joinAsync(null);
    }

    default CompletableFuture<VoiceConnection> joinAsync(VoiceJoinRequest request) {
        var body = request != null ? Kook4jCodec.toJsonObject(request) : new JsonObject();
        body.addProperty("channel_id", channelId());
        return http().postAsync("/voice/join", body)
                .thenApply(data -> Kook4jCodec.fromJson(data, VoiceConnection.class));
    }

    default CompletableFuture<Void> leaveAsync() {
        var body = new JsonObject();
        body.addProperty("channel_id", channelId());
        return http().postAsync("/voice/leave", body).thenAccept(__ -> {
        });
    }

    default CompletableFuture<Void> keepAliveAsync() {
        var body = new JsonObject();
        body.addProperty("channel_id", channelId());
        return http().postAsync("/voice/keep-alive", body).thenAccept(__ -> {
        });
    }

    default VoiceConnection join() {
        return AbstractScope.sync(joinAsync());
    }

    default VoiceConnection join(VoiceJoinRequest request) {
        return AbstractScope.sync(joinAsync(request));
    }

    default void leave() {
        AbstractScope.sync(leaveAsync());
    }

    default void keepAlive() {
        AbstractScope.sync(keepAliveAsync());
    }
}