package bot.inker.kook4j.event.dm;

import bot.inker.kook4j.event.SystemEvent;

public final class DirectMessageDeleteEvent extends SystemEvent {

    private String msgId;
    private String authorId;
    private String targetId;
    private String chatCode;
    private long deletedAt;

    DirectMessageDeleteEvent() {
    }

    public String msgId() {
        return msgId;
    }

    public String authorId() {
        return authorId;
    }

    public String targetId() {
        return targetId;
    }

    public String chatCode() {
        return chatCode;
    }

    public long deletedAt() {
        return deletedAt;
    }
}
