package bot.inker.kook4j.entity;

public final class Reaction {

    private Emoji emoji;
    private int count;
    private boolean me;

    Reaction() {
    }

    public Emoji emoji() {
        return emoji;
    }

    public int count() {
        return count;
    }

    public boolean me() {
        return me;
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
