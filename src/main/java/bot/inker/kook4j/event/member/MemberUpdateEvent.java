package bot.inker.kook4j.event.member;

import bot.inker.kook4j.event.SystemEvent;

public final class MemberUpdateEvent extends SystemEvent {

    private String userId;
    private String nickname;

    MemberUpdateEvent() {
    }

    public String userId() {
        return userId;
    }

    public String nickname() {
        return nickname;
    }
}
