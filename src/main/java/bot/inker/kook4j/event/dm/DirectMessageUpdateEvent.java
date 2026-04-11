package bot.inker.kook4j.event.dm;

import bot.inker.kook4j.event.SystemEvent;

public final class DirectMessageUpdateEvent extends SystemEvent {

    private String msgId;
    private String authorId;
    private String targetId;
    private String content;
    private String chatCode;
    private long updatedAt;

    DirectMessageUpdateEvent() {
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

    public String content() {
        return content;
    }

    public String chatCode() {
        return chatCode;
    }

    public long updatedAt() {
        return updatedAt;
    }
}
