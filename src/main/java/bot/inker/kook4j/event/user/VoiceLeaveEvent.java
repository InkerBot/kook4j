package bot.inker.kook4j.event.user;

import bot.inker.kook4j.event.SystemEvent;

public final class VoiceLeaveEvent extends SystemEvent {

    private String userId;
    private String channelId;

    VoiceLeaveEvent() {
    }

    public String userId() {
        return userId;
    }

    public String channelId() {
        return channelId;
    }
}
