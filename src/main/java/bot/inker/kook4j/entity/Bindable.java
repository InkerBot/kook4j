package bot.inker.kook4j.entity;

import bot.inker.kook4j.http.HttpClient;

public interface Bindable {
    Bindable bind(HttpClient http);
}
