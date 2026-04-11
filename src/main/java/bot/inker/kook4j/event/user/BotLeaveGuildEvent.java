package bot.inker.kook4j.event.user;

import bot.inker.kook4j.event.SystemEvent;

public final class BotLeaveGuildEvent extends SystemEvent {

    private String guildId;

    BotLeaveGuildEvent() {
    }

    public String guildId() {
        return guildId;
    }
}
