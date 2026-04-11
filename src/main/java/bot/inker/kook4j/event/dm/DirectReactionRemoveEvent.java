package bot.inker.kook4j.event.dm;

import bot.inker.kook4j.event.SystemEvent;

public final class DirectReactionRemoveEvent extends SystemEvent {

    private String msgId;
    private String userId;
    private String chatCode;
    private Emoji emoji;

    DirectReactionRemoveEvent() {
    }

    public String msgId() {
        return msgId;
    }

    public String userId() {
        return userId;
    }

    public String chatCode() {
        return chatCode;
    }

    public Emoji emoji() {
        return emoji;
    }

    public static final class Emoji {

        private String id;
        private String name;

        Emoji() {
        }

        public String id() {
            return id;
        }

        public String name() {
            return name;
        }
    }
}
