package bot.inker.kook4j.event.guild;

import bot.inker.kook4j.event.SystemEvent;

import java.util.List;

public final class BlockListRemoveEvent extends SystemEvent {

    private String operatorId;
    private List<String> userId;

    BlockListRemoveEvent() {
    }

    public String operatorId() {
        return operatorId;
    }

    public List<String> userId() {
        return userId;
    }
}
