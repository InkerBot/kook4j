package bot.inker.kook4j.scope;

import bot.inker.kook4j.BotInstance;
import bot.inker.kook4j.exception.KookApiException;

final class IntegrationTestHelper {

    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MS = 500;

    private IntegrationTestHelper() {}

    static String sendAndSettle(BotInstance bot, String channelId, String content) {
        String msgId = bot.channel(channelId).send(content);
        settle(bot, msgId);
        return msgId;
    }

    static void settle(BotInstance bot, String msgId) {
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                bot.message(msgId).view();
                return;
            } catch (KookApiException e) {
                if (e.code() != 40012 || i == MAX_RETRIES - 1) throw e;
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ie);
                }
            }
        }
    }
}