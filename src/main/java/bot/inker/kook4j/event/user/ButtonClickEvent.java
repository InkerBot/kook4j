package bot.inker.kook4j.event.user;

import bot.inker.kook4j.event.SystemEvent;

public final class ButtonClickEvent extends SystemEvent {

    private String msgId;
    private String userId;
    private String value;
    private String targetId;

    ButtonClickEvent() {
    }

    public String msgId() {
        return msgId;
    }

    public String userId() {
        return userId;
    }

    public String value() {
        return value;
    }

    public String targetId() {
        return targetId;
    }
}
