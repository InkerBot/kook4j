package bot.inker.kook4j.scope;

import bot.inker.kook4j.exception.KookConnectionException;
import bot.inker.kook4j.http.HttpClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public interface AbstractScope {

    static <T> T sync(CompletableFuture<T> future) {
        try {
            return future.join();
        } catch (CompletionException e) {
            var cause = e.getCause();
            if (cause instanceof RuntimeException re) throw re;
            throw new KookConnectionException("Unexpected error", cause);
        }
    }

    HttpClient http();
}
