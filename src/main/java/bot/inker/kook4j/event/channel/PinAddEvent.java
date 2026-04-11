package bot.inker.kook4j.event.channel;

import bot.inker.kook4j.event.SystemEvent;

public final class PinAddEvent extends SystemEvent {

    private String channelId;
    private String operatorId;
    private String msgId;

    PinAddEvent() {
    }

    public String channelId() {
        return channelId;
    }

    public String operatorId() {
        return operatorId;
    }

    public String msgId() {
        return msgId;
    }
}
