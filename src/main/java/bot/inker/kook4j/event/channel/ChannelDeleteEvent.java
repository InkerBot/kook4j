package bot.inker.kook4j.event.channel;

import bot.inker.kook4j.event.SystemEvent;

public final class ChannelDeleteEvent extends SystemEvent {

    private String id;
    private long deletedAt;

    ChannelDeleteEvent() {
    }

    public String id() {
        return id;
    }

    public long deletedAt() {
        return deletedAt;
    }
}
