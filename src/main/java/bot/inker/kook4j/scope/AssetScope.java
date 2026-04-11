package bot.inker.kook4j.scope;

import bot.inker.kook4j.http.HttpClient;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public record AssetScope(HttpClient http) implements AbstractScope {

    public CompletableFuture<String> uploadAsync(File file) {
        return http.postFormAsync("/asset/create", Map.of(), file)
                .thenApply(data -> data.getAsJsonObject().get("url").getAsString());
    }

    public String upload(File file) {
        return AbstractScope.sync(uploadAsync(file));
    }
}
