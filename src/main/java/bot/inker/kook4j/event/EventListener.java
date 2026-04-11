package bot.inker.kook4j.event;

@FunctionalInterface
public interface EventListener<T extends Event> {

    void onEvent(T event);
}
