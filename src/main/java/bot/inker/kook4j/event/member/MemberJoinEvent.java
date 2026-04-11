package bot.inker.kook4j.event.member;

import bot.inker.kook4j.event.SystemEvent;

public final class MemberJoinEvent extends SystemEvent {

    private String userId;
    private long joinedAt;

    MemberJoinEvent() {
    }

    public String userId() {
        return userId;
    }

    public long joinedAt() {
        return joinedAt;
    }
}
