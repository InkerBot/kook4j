package bot.inker.kook4j.event.guild;

import bot.inker.kook4j.event.SystemEvent;

import java.util.List;

public final class BlockListAddEvent extends SystemEvent {

    private String operatorId;
    private List<String> remark;
    private List<String> userId;

    BlockListAddEvent() {
    }

    public String operatorId() {
        return operatorId;
    }

    public List<String> remark() {
        return remark;
    }

    public List<String> userId() {
        return userId;
    }
}
