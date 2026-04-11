package bot.inker.kook4j.scope;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface GuildBadgeScope extends AbstractScope {

    String guildId();

    default CompletableFuture<byte[]> badgeAsync() {
        return badgeAsync(0);
    }

    default CompletableFuture<byte[]> badgeAsync(int style) {
        return http().getRawBytesAsync("/badge/guild", Map.of("guild_id", guildId(), "style", String.valueOf(style)));
    }

    default byte[] badge() {
        return AbstractScope.sync(badgeAsync());
    }

    default byte[] badge(int style) {
        return AbstractScope.sync(badgeAsync(style));
    }
}
