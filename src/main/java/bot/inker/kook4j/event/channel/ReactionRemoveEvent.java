package bot.inker.kook4j.event.channel;

import bot.inker.kook4j.event.SystemEvent;

public final class ReactionRemoveEvent extends SystemEvent {

    private String msgId;
    private String userId;
    private String channelId;
    private Emoji emoji;

    ReactionRemoveEvent() {
    }

    public String msgId() {
        return msgId;
    }

    public String userId() {
        return userId;
    }

    public String channelId() {
        return channelId;
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
