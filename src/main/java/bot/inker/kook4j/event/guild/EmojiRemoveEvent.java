package bot.inker.kook4j.event.guild;

import bot.inker.kook4j.event.SystemEvent;

public final class EmojiRemoveEvent extends SystemEvent {

    private String id;

    EmojiRemoveEvent() {
    }

    public String id() {
        return id;
    }
}
