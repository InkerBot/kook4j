package bot.inker.kook4j.event.guild;

import bot.inker.kook4j.event.SystemEvent;

public final class EmojiUpdateEvent extends SystemEvent {

    private String id;
    private String name;

    EmojiUpdateEvent() {
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }
}
