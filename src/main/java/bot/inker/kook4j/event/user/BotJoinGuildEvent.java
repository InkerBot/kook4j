package bot.inker.kook4j.event.user;

import bot.inker.kook4j.event.SystemEvent;

public final class BotJoinGuildEvent extends SystemEvent {

    private String guildId;

    BotJoinGuildEvent() {
    }

    public String guildId() {
        return guildId;
    }
}
