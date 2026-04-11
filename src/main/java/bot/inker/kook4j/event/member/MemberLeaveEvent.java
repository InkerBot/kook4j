package bot.inker.kook4j.event.member;

import bot.inker.kook4j.event.SystemEvent;

public final class MemberLeaveEvent extends SystemEvent {

    private String userId;
    private long exitedAt;

    MemberLeaveEvent() {
    }

    public String userId() {
        return userId;
    }

    public long exitedAt() {
        return exitedAt;
    }
}
