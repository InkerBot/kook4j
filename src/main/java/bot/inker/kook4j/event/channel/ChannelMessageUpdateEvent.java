package bot.inker.kook4j.event.channel;

import bot.inker.kook4j.event.SystemEvent;

import java.util.List;

public final class ChannelMessageUpdateEvent extends SystemEvent {

    private String msgId;
    private String content;
    private String channelId;
    private List<String> mention;
    private boolean mentionAll;
    private List<Integer> mentionRoles;
    private boolean mentionHere;
    private long updatedAt;

    ChannelMessageUpdateEvent() {
    }

    public String msgId() {
        return msgId;
    }

    public String content() {
        return content;
    }

    public String channelId() {
        return channelId;
    }

    public List<String> mention() {
        return mention;
    }

    public boolean mentionAll() {
        return mentionAll;
    }

    public List<Integer> mentionRoles() {
        return mentionRoles;
    }

    public boolean mentionHere() {
        return mentionHere;
    }

    public long updatedAt() {
        return updatedAt;
    }
}
