package bot.inker.kook4j.event.channel;

import bot.inker.kook4j.event.SystemEvent;

public final class ChannelMessageDeleteEvent extends SystemEvent {

    private String msgId;
    private String channelId;

    ChannelMessageDeleteEvent() {
    }

    public String msgId() {
        return msgId;
    }

    public String channelId() {
        return channelId;
    }
}
