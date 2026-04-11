package bot.inker.kook4j.http;

import com.google.gson.JsonElement;

public final class ApiResponse {

    private int code;
    private String message;
    private JsonElement data;

    ApiResponse() {
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

    public JsonElement data() {
        return data;
    }

    public boolean success() {
        return code == 0;
    }
}
