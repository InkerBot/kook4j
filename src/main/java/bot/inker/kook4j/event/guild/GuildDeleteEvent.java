package bot.inker.kook4j.event.guild;

import bot.inker.kook4j.event.SystemEvent;

public final class GuildDeleteEvent extends SystemEvent {

    private String id;
    private long deletedAt;

    GuildDeleteEvent() {
    }

    public String id() {
        return id;
    }

    public long deletedAt() {
        return deletedAt;
    }
}
